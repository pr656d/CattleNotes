package com.pr656d.shared.data.breeding.datasources

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInsemination
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.shared.data.db.BreedingDao
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for [Breeding].
 */
interface BreedingDataSource {
    /**
    * Load breeding list from data source and save to local db.
    */
    fun load(onComplete: () -> Unit = {})

    /**
     * Add breeding at remote data source.
     */
    fun addBreeding(breeding: Breeding)

    /**
     * Delete breeding at remote data source.
     */
    fun deleteBreeding(breeding: Breeding)

    /**
     * Update breeding at remote data source.
     */
    fun updateBreeding(breeding: Breeding)
}

@Singleton
class FirestoreBreedingDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    private val breedingDao: BreedingDao
) : BreedingDataSource {
    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at cattle data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override fun load(onComplete: () -> Unit) {
        val onSuccessListener: (QuerySnapshot) -> Unit = { snapshot ->
            DefaultScheduler.execute {
                val breedingList = snapshot.documents.map { getBreeding(it) }
                breedingDao.insertAll(breedingList).also {
                    onComplete()
                }
            }
        }

        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener {
                    Timber.d("load() failed at breeding data source : ${it.localizedMessage}")
                }
        }
    }

    override fun addBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document(breeding.id)
                .set(breeding.asHashMap())
                .addOnFailureListener {
                    Timber.d("addBreeding() failed : ${it.localizedMessage}")
                }
        }
    }

    override fun deleteBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document(breeding.id)
                .delete()
                .addOnFailureListener {
                    Timber.d("deleteBreeding() failed() : ${it.localizedMessage}")
                }
        }
    }

    override fun updateBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document(breeding.id)
                .set(breeding.asHashMap(), SetOptions.merge())
                .addOnFailureListener {
                    Timber.d("updateBreeding() failed() : ${it.localizedMessage}")
                }
        }
    }

    private fun getBreeding(document: DocumentSnapshot): Breeding {
        return document.run {
            Breeding(
                cattleId = get(KEY_CATTLE_ID) as String,
                artificialInsemination = (get(KEY_ARTIFICIAL_INSEMINATION) as HashMap<*, *>).let { ai ->
                    ArtificialInsemination(
                        (ai[KEY_AI_DATE] as Long).toLocalDate(),
                        didBy = ai[KEY_AI_DID_BY] as? String,
                        bullName = ai[KEY_AI_BULL_NAME] as? String,
                        strawCode = ai[KEY_AI_STRAW_CODE] as? String
                    )
                },
                repeatHeat = (get(KEY_REPEAT_HEAT) as HashMap<*, *>).let { data ->
                    BreedingEvent.RepeatHeat(
                        expectedOn = (data[KEY_BREEDING_EVENT_EXPECTED_ON] as Long).toLocalDate(),
                        status = data[KEY_BREEDING_EVENT_STATUS] as? Boolean,
                        doneOn = (data[KEY_BREEDING_EVENT_DONE_ON] as? Long)?.toLocalDate()
                    )
                },
                pregnancyCheck = (get(KEY_PREGNANCY_CHECK) as HashMap<*, *>).let { data ->
                    BreedingEvent.PregnancyCheck(
                        expectedOn = (data[KEY_BREEDING_EVENT_EXPECTED_ON] as Long).toLocalDate(),
                        status = data[KEY_BREEDING_EVENT_STATUS] as? Boolean,
                        doneOn = (data[KEY_BREEDING_EVENT_DONE_ON] as? Long)?.toLocalDate()
                    )
                },
                dryOff = (get(KEY_DRY_OFF) as HashMap<*, *>).let { data ->
                    BreedingEvent.DryOff(
                        expectedOn = (data[KEY_BREEDING_EVENT_EXPECTED_ON] as Long).toLocalDate(),
                        status = data[KEY_BREEDING_EVENT_STATUS] as? Boolean,
                        doneOn = (data[KEY_BREEDING_EVENT_DONE_ON] as? Long)?.toLocalDate()
                    )
                },
                calving = (get(KEY_CALVING) as HashMap<*, *>).let { data ->
                    BreedingEvent.Calving(
                        expectedOn = (data[KEY_BREEDING_EVENT_EXPECTED_ON] as Long).toLocalDate(),
                        status = data[KEY_BREEDING_EVENT_STATUS] as? Boolean,
                        doneOn = (data[KEY_BREEDING_EVENT_DONE_ON] as? Long)?.toLocalDate()
                    )
                }
            ).apply { id = document.id }
        }
    }

   /* private fun parseBreedingEvent(data: HashMap<*, *>) {
        return BreedingEvent(
            expectedOn = (data[KEY_BREEDING_EVENT_EXPECTED_ON] as Long).toLocalDate(),
            status = data[KEY_BREEDING_EVENT_STATUS] as? Boolean,
            doneOn = (data[KEY_BREEDING_EVENT_DONE_ON] as? Long)?.toLocalDate()
        )
    }*/

    private fun Breeding.asHashMap(): HashMap<String, Any?> = hashMapOf<String, Any?>().apply {
        put(KEY_CATTLE_ID, cattleId)
        put(
            KEY_ARTIFICIAL_INSEMINATION,
            hashMapOf<String, Any?>().apply {
                put(KEY_AI_DATE, artificialInsemination.date.toLong())
                put(KEY_AI_DID_BY, artificialInsemination.didBy)
                put(KEY_AI_BULL_NAME, artificialInsemination.bullName)
                put(KEY_AI_STRAW_CODE, artificialInsemination.strawCode)
            }
        )
        put(
            KEY_REPEAT_HEAT,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, repeatHeat.expectedOn.toLong())
                put(KEY_BREEDING_EVENT_STATUS, repeatHeat.status)
                put(KEY_BREEDING_EVENT_DONE_ON, repeatHeat.doneOn?.toLong())
            }
        )
        put(
            KEY_PREGNANCY_CHECK,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, pregnancyCheck.expectedOn.toLong())
                put(KEY_BREEDING_EVENT_STATUS, pregnancyCheck.status)
                put(KEY_BREEDING_EVENT_DONE_ON, pregnancyCheck.doneOn?.toLong())
            }
        )
        put(
            KEY_DRY_OFF,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, dryOff.expectedOn.toLong())
                put(KEY_BREEDING_EVENT_STATUS, dryOff.status)
                put(KEY_BREEDING_EVENT_DONE_ON, dryOff.doneOn?.toLong())
            }
        )
        put(
            KEY_CALVING,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, calving.expectedOn.toLong())
                put(KEY_BREEDING_EVENT_STATUS, calving.status)
                put(KEY_BREEDING_EVENT_DONE_ON, calving.doneOn?.toLong())
            }
        )
        put(KEY_BREEDING_COMPLETED, breedingCompleted)
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
        private const val KEY_BREEDING_COMPLETED = "breedingCompleted"
    }
}