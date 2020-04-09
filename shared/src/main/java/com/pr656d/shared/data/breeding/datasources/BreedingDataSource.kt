package com.pr656d.shared.data.breeding.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Breeding
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.TimeUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for [Breeding].
 */
interface BreedingDataSource {
    fun addBreeding(breeding: Breeding)
    fun deleteBreeding(breeding: Breeding)
    fun updateBreeding(breeding: Breeding)
}

@Singleton
class FirestoreBreedingDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore
) : BreedingDataSource {
    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at cattle data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override fun addBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document()
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
                    Timber.d("deleteBreeding() failed() : ${it.localizedMessage}")
                }
        }
    }

    private fun Breeding.asHashMap(): HashMap<String, Any?> = hashMapOf<String, Any?>().apply {
        put(KEY_CATTLE_ID, cattleId)
        put(
            KEY_ARTIFICIAL_INSEMINATION,
            hashMapOf<String, Any?>().apply {
                put(KEY_AI_DATE, artificialInsemination?.date?.let {
                    TimeUtils.toEpochMillis(it)
                })
                put(KEY_AI_DID_BY, artificialInsemination?.didBy)
                put(KEY_AI_BULL_NAME, artificialInsemination?.bullName)
                put(KEY_AI_STRAW_CODE, artificialInsemination?.strawCode)
            }
        )
        put(
            KEY_REPEAT_HEAT,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, repeatHeat?.expectedOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
                put(KEY_BREEDING_EVENT_STATUS, repeatHeat?.status)
                put(KEY_BREEDING_EVENT_DONE_ON, repeatHeat?.doneOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
            }
        )
        put(
            KEY_PREGNANCY_CHECK,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, pregnancyCheck?.expectedOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
                put(KEY_BREEDING_EVENT_STATUS, pregnancyCheck?.status)
                put(KEY_BREEDING_EVENT_DONE_ON, pregnancyCheck?.doneOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
            }
        )
        put(
            KEY_DRY_OFF,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, dryOff?.expectedOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
                put(KEY_BREEDING_EVENT_STATUS, dryOff?.status)
                put(KEY_BREEDING_EVENT_DONE_ON, dryOff?.doneOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
            }
        )
        put(
            KEY_CALVING,
            hashMapOf<String, Any?>().apply {
                put(KEY_BREEDING_EVENT_EXPECTED_ON, calving?.expectedOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
                put(KEY_BREEDING_EVENT_STATUS, calving?.status)
                put(KEY_BREEDING_EVENT_DONE_ON, calving?.doneOn?.let {
                    TimeUtils.toEpochMillis(it)
                })
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