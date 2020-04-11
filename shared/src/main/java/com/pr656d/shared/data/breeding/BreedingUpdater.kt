package com.pr656d.shared.data.breeding

import com.google.firebase.firestore.*
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInseminationInfo
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.TimeUtils
import timber.log.Timber
import javax.inject.Inject

interface BreedingUpdater {
    fun initialize()

    fun stop()
}

class FirestoreBreedingUpdater @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authIdDataSource: AuthIdDataSource,
    private val breedingRepository: BreedingRepository
) : BreedingUpdater {

    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("user Id not found at cattle updater")
            throw IllegalArgumentException("user Id not found at cattle updater")
        }
    }

    private var registration: ListenerRegistration? = null

    override fun initialize() {
        Timber.d("Initializing BreedingUpdater")

        // Remove previous subscriptions if exists
        registration?.remove()

        val breedingListListener =
            { snapshots: QuerySnapshot?, e: FirebaseFirestoreException? ->
                DefaultScheduler.execute {
                    if (e != null) {
                        Timber.d("breedingList listener error : ${e.printStackTrace()}")
                        return@execute
                    }

                    Timber.d("Executing BreedingUpdater listener")

                    for (doc in snapshots!!.documentChanges) {
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> {
                                breedingRepository.addBreeding(
                                    breeding = getBreeding(doc.document),
                                    saveToLocal = true
                                )

                                Timber.d("adding breeding...")
                            }
                            DocumentChange.Type.MODIFIED -> {
                                breedingRepository.updateBreeding(
                                    breeding = getBreeding(doc.document),
                                    saveToLocal = true
                                )

                                Timber.d("updating breeding...")
                            }
                            DocumentChange.Type.REMOVED -> {
                                breedingRepository.deleteBreeding(
                                    breeding = getBreeding(doc.document),
                                    saveToLocal = true
                                )

                                Timber.d("deleting breeding...")
                            }
                        }
                    }
                }
            }

        // All Firestore operations start from the main thread to avoid concurrency issues.
        // To ignore Foreign key concurrency issue use 1s delay. Let cattle fetched first.
        // TODO("Temporary Fix") : Use more promise able way to handle concurrency.
        DefaultScheduler.postDelayedToMainThread(1000) {
            registration = firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .addSnapshotListener(breedingListListener)
        }

        Timber.d("Initialized BreedingUpdater")
    }

    override fun stop() {
        registration?.remove()
        Timber.d("Removed snapshot listener registration for BreedingUpdater")
    }

    private fun getBreeding(document: QueryDocumentSnapshot): Breeding {
        return document.data.run {
            Breeding(
                cattleId = get(KEY_CATTLE_ID) as String,
                artificialInsemination = (get(KEY_ARTIFICIAL_INSEMINATION) as HashMap<*, *>).let { ai ->
                    ArtificialInseminationInfo(
                        (ai[KEY_AI_DATE] as Long).let { TimeUtils.toLocalDate(it) },
                        didBy = ai[KEY_AI_DID_BY] as? String,
                        bullName = ai[KEY_AI_BULL_NAME] as? String,
                        strawCode = ai[KEY_AI_STRAW_CODE] as? String
                    )
                },
                repeatHeat = parseBreedingEvent(get(KEY_REPEAT_HEAT) as HashMap<*, *>),
                pregnancyCheck = parseBreedingEvent(get(KEY_PREGNANCY_CHECK) as HashMap<*, *>),
                dryOff = parseBreedingEvent(get(KEY_DRY_OFF) as HashMap<*, *>),
                calving = parseBreedingEvent(get(KEY_CALVING) as HashMap<*, *>)
            ).apply { id = document.id }
        }
    }

    private fun parseBreedingEvent(data: HashMap<*, *>): BreedingEvent {
        return BreedingEvent(
            (data[KEY_BREEDING_EVENT_EXPECTED_ON] as Long).let {
                TimeUtils.toLocalDate(it)
            },
            data[KEY_BREEDING_EVENT_STATUS] as? Boolean,
            (data[KEY_BREEDING_EVENT_DONE_ON] as? Long)?.let {
                TimeUtils.toLocalDate(it)
            }
        )
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val BREEDING_COLLECTION = "breedingList"
        private const val KEY_CATTLE_ID = "cattleId"
        private const val KEY_ARTIFICIAL_INSEMINATION = "artificialInsemination"
        private const val KEY_AI_DATE = "date"
        private const val KEY_AI_DID_BY = "didBy"
        private const val KEY_AI_BULL_NAME = "bullName"
        private const val KEY_AI_STRAW_CODE = "strawCode"
        private const val KEY_REPEAT_HEAT = "repeatHeat"
        private const val KEY_PREGNANCY_CHECK = "pregnancyCheck"
        private const val KEY_DRY_OFF = "dryOff"
        private const val KEY_CALVING = "calving"
        private const val KEY_BREEDING_EVENT_EXPECTED_ON = "expectedOn"
        private const val KEY_BREEDING_EVENT_STATUS = "status"
        private const val KEY_BREEDING_EVENT_DONE_ON = "doneOn"
    }
}