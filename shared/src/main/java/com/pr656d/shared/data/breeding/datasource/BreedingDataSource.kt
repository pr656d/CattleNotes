/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.data.breeding.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInsemination
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.di.MainDispatcher
import com.pr656d.shared.utils.FirestoreUtil.BATCH_OPERATION_LIMIT
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Remote data source for [Breeding].
 * @see FirestoreBreedingDataSource
 */
interface BreedingDataSource {
    /**
     * Load breeding list from data source.
     */
    suspend fun load(): List<Breeding>

    /**
     * Add breeding at remote data source.
     */
    suspend fun addBreeding(breeding: Breeding)

    /**
     * Add all breeding at remote data source.
     */
    suspend fun addAllBreeding(breedingList: List<Breeding>)

    /**
     * Delete breeding at remote data source.
     */
    suspend fun deleteBreeding(breeding: Breeding)

    /**
     * Update breeding at remote data source.
     */
    suspend fun updateBreeding(breeding: Breeding)
}

@Singleton
class FirestoreBreedingDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : BreedingDataSource {

    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at cattle data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override suspend fun load(): List<Breeding> = withContext(mainDispatcher) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        suspendCancellableCoroutine<List<Breeding>> { continuation ->
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!continuation.isActive) return@addOnSuccessListener

                    val list = snapshot.documents.map { getBreeding(it) }
                    continuation.resume(list)
                }
                .addOnFailureListener {
                    Timber.d("load() failed at breeding data source : ${it.message}")

                    if (!continuation.isActive) return@addOnFailureListener

                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document(breeding.id)
                .set(breeding.asHashMap())
                .addOnSuccessListener {
                    Timber.d("Breeding : ${breeding.id} added successfully")
                }
                .addOnFailureListener {
                    Timber.d("addBreeding() failed : ${it.message}")
                }
        }
    }

    override suspend fun addAllBreeding(breedingList: List<Breeding>) {
        val chunks = breedingList.chunked(BATCH_OPERATION_LIMIT)

        chunks.forEach { chunk ->
            firestore
                .runBatch { writeBatch ->
                    chunk.forEach { breeding ->
                        val ref = firestore
                            .collection("$USERS_COLLECTION/$userId/$BREEDING_COLLECTION")
                            .document(breeding.id)

                        writeBatch.set(ref, breeding.asHashMap(), SetOptions.merge())
                    }
                }.addOnSuccessListener {
                    Timber.d("All ${breedingList.count()} added successfully")
                }.addOnFailureListener {
                    Timber.d("addAllCattle() failed : ${it.message}")
                }
        }
    }

    override suspend fun deleteBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document(breeding.id)
                .delete()
                .addOnSuccessListener {
                    Timber.d("Breeding : ${breeding.id} deleted successfully")
                }
                .addOnFailureListener {
                    Timber.d("deleteBreeding() failed : ${it.message}")
                }
        }
    }

    override suspend fun updateBreeding(breeding: Breeding) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(BREEDING_COLLECTION)
                .document(breeding.id)
                .set(breeding.asHashMap(), SetOptions.merge())
                .addOnSuccessListener {
                    Timber.d("Breeding : ${breeding.id} updated successfully")
                }
                .addOnFailureListener {
                    Timber.d("updateBreeding() failed : ${it.message}")
                }
        }
    }

    private fun getBreeding(document: DocumentSnapshot): Breeding {
        return document.run {
            Breeding(
                id = document.id,
                cattleId = get(KEY_CATTLE_ID) as String,
                artificialInsemination = (get(KEY_ARTIFICIAL_INSEMINATION) as HashMap<*, *>)
                    .let { ai ->
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
            )
        }
    }

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
