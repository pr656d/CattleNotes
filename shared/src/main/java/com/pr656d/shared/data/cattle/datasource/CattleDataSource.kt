/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.shared.data.cattle.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Cattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.utils.FirestoreUtil.BATCH_OPERATION_LIMIT
import com.pr656d.shared.utils.toGroup
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import com.pr656d.shared.utils.toType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Remote data source for [Cattle].
 * @see FirestoreCattleDataSource
 */
interface CattleDataSource {
    /**
     * Load cattle list from data source.
     */
    suspend fun load(): List<Cattle>

    /**
     * Add cattle at remote data source.
     */
    suspend fun addCattle(cattle: Cattle)

    /**
     * Add all cattle at remote data source.
     */
    suspend fun addAllCattle(cattleList: List<Cattle>)

    /**
     * Delete cattle at remote data source.
     */
    suspend fun deleteCattle(cattle: Cattle)

    /**
     * Update cattle at remote data source.
     */
    suspend fun updateCattle(cattle: Cattle)
}

@Singleton
class FirestoreCattleDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    private val breedingRepository: BreedingRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : CattleDataSource {

    private val mainDispatcher = Dispatchers.Main

    private val scope = CoroutineScope(defaultDispatcher + SupervisorJob())

    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at cattle data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override suspend fun load(): List<Cattle> = withContext(mainDispatcher) {
        suspendCancellableCoroutine<List<Cattle>> { continuation ->
            // All Firestore operations start from the main thread to avoid concurrency issues.
            firestore
                .collection("$USERS_COLLECTION/$userId/$CATTLE_COLLECTION")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!continuation.isActive) return@addOnSuccessListener

                    val cattleList = snapshot.documents.map { getCattle(it) }
                    continuation.resume(cattleList)
                }
                .addOnFailureListener {
                    Timber.d("load() failed at cattle data source : ${it.message}")
                    if (!continuation.isActive) return@addOnFailureListener
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection("$USERS_COLLECTION/$userId/$CATTLE_COLLECTION")
                .document(cattle.id)
                .set(cattle.asHashMap())
                .addOnSuccessListener {
                    Timber.d("Cattle : ${cattle.id} added successfully")
                }
                .addOnFailureListener {
                    Timber.d("addCattle() failed : ${it.message}")
                }
        }
    }

    override suspend fun addAllCattle(cattleList: List<Cattle>) {
        val chunks = cattleList.chunked(BATCH_OPERATION_LIMIT)

        chunks.forEach { list ->
            firestore.runBatch { writeBatch ->
                list.forEach { cattle ->
                    val ref = firestore
                        .collection("$USERS_COLLECTION/$userId/$CATTLE_COLLECTION")
                        .document(cattle.id)

                    writeBatch.set(ref, cattle.asHashMap(), SetOptions.merge())
                }
            }.addOnSuccessListener {
                Timber.d("All ${cattleList.count()} added successfully")
            }.addOnFailureListener {
                Timber.d("addAllCattle() failed : ${it.message}")
            }
        }
    }

    override suspend fun deleteCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection("$USERS_COLLECTION/$userId/$CATTLE_COLLECTION")
                .document(cattle.id)
                .delete()
                .addOnSuccessListener {
                    Timber.d("Cattle : ${cattle.id} deleted successfully")
                    scope.launch {
                        deleteBreedingOfCattle(cattle)
                    }
                }
                .addOnFailureListener {
                    Timber.d("deleteCattle() failed : ${it.message}")
                }
        }
    }

    override suspend fun updateCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection("$USERS_COLLECTION/$userId/$CATTLE_COLLECTION")
                .document(cattle.id)
                .set(cattle.asHashMap(), SetOptions.merge())
                .addOnSuccessListener {
                    Timber.d("Cattle : ${cattle.id} updated successfully")
                }
                .addOnFailureListener {
                    Timber.d("updateCattle() failed : ${it.message}")
                }
        }
    }

    private suspend fun deleteBreedingOfCattle(cattle: Cattle) {
        Timber.d("Deleting breeding data for cattle : ${cattle.id}")

        val list = breedingRepository
            .getAllBreedingByCattleId(cattle.id)
            .firstOrNull()
            ?.map {
                firestore
                    .collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(BREEDING_COLLECTION)
                    .document(it.id)
            } ?: return

        // Make chunks
        val chunks = list.chunked(BATCH_OPERATION_LIMIT)

        chunks.forEach { chunk ->
            firestore
                .runBatch { writeBatch ->
                    chunk.forEach { ref ->
                        writeBatch.delete(ref)
                    }
                }
                .addOnSuccessListener {
                    Timber.d("Deleted breeding data for cattle : ${cattle.id}")
                }
                .addOnFailureListener {
                    Timber.e("Failed to delete breeding data for cattle : ${cattle.id}")
                }
        }

    }

    private fun getCattle(doc: DocumentSnapshot): Cattle {
        return doc.run {
            Cattle(
                id = id,
                tagNumber = get(KEY_TAG_NUMBER) as Long,
                name = get(KEY_NAME) as? String,
                image = (get(KEY_IMAGE_URL) as? String)?.let { Cattle.Image(null, it) },
                type = (get(KEY_TYPE) as String).toType(),
                breed = (get(KEY_BREED) as String),
                group = (get(KEY_GROUP) as? String)?.toGroup(),
                lactation = get(KEY_LACTATION) as? Long,
                homeBorn = get(KEY_HOME_BORN) as Boolean,
                purchaseAmount = get(KEY_PURCHASE_AMOUNT) as? Long,
                purchaseDate = (get(KEY_PURCHASE_Date) as? Long)?.toLocalDate(),
                dateOfBirth = (get(KEY_DOB) as? Long)?.toLocalDate(),
                parent = get(KEY_PARENT) as? String
            )
        }
    }

    private fun Cattle.asHashMap(): HashMap<String, Any?> = hashMapOf<String, Any?>().apply {
        put(KEY_TAG_NUMBER, tagNumber)
        put(KEY_NAME, name)
        put(KEY_IMAGE_URL, image?.remotePath)
        put(KEY_TYPE, type.displayName)
        put(KEY_BREED, breed)
        put(KEY_GROUP, group?.displayName)
        put(KEY_LACTATION, lactation)
        put(KEY_HOME_BORN, homeBorn)
        put(KEY_PURCHASE_AMOUNT, purchaseAmount)
        put(KEY_PURCHASE_Date, purchaseDate?.toLong())
        put(KEY_DOB, dateOfBirth?.toLong())
        put(KEY_PARENT, parent)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val CATTLE_COLLECTION = "cattleList"
        private const val BREEDING_COLLECTION = "breedingList"
        private const val KEY_BREEDING_CATTLE_ID = "cattleId"
        private const val KEY_TAG_NUMBER = "tagNumber"
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE_URL = "imageUrl"
        private const val KEY_TYPE = "type"
        private const val KEY_BREED = "breed"
        private const val KEY_GROUP = "group"
        private const val KEY_LACTATION = "lactation"
        private const val KEY_HOME_BORN = "homeBorn"
        private const val KEY_PURCHASE_AMOUNT = "purchaseAmount"
        private const val KEY_PURCHASE_Date = "purchaseDate"
        private const val KEY_DOB = "dateOfBirth"
        private const val KEY_PARENT = "parentId"
    }
}