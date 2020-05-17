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

import androidx.annotation.MainThread
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Cattle
import com.pr656d.shared.data.db.CattleDao
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.toGroup
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import com.pr656d.shared.utils.toType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for [Cattle].
 * @see FirestoreCattleDataSource
 */
interface CattleDataSource {
    /**
     * Load cattle list from data source and save to local db.
     */
    fun load(onComplete: () -> Unit = {})

    /**
     * Add cattle at remote data source.
     */
    fun addCattle(cattle: Cattle)

    /**
     * Delete cattle at remote data source.
     */
    fun deleteCattle(cattle: Cattle)

    /**
     * Update cattle at remote data source.
     */
    fun updateCattle(cattle: Cattle)
}

@Singleton
class FirestoreCattleDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    private val cattleDao: CattleDao
) : CattleDataSource {
    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at cattle data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override fun load(onComplete: () -> Unit) {
        val onSuccessListener: (QuerySnapshot) -> Unit = { snapshot ->
            DefaultScheduler.execute {
                val cattleList = snapshot.documents.map { getCattle(it) }
                cattleDao.insertAll(cattleList).also {
                    onComplete()
                }
            }
        }

        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(CATTLE_COLLECTION)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener {
                    Timber.d("load() failed at cattle data source : ${it.localizedMessage}")
                }
        }
    }

    override fun addCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(CATTLE_COLLECTION)
                .document(cattle.id)
                .set(cattle.asHashMap())
                .addOnFailureListener {
                    Timber.d("addCattle() failed : ${it.localizedMessage}")
                }
        }
    }

    override fun deleteCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(CATTLE_COLLECTION)
                .document(cattle.id)
                .delete()
                .addOnSuccessListener {
                    Timber.d("Cattle : ${cattle.id} deleted successfully")
                    deleteBreedingOfCattle(cattle)
                }
                .addOnFailureListener {
                    Timber.d("deleteCattle() failed : ${it.localizedMessage}")
                }
        }
    }

    override fun updateCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(CATTLE_COLLECTION)
                .document(cattle.id)
                .set(cattle.asHashMap(), SetOptions.merge())
                .addOnFailureListener {
                    Timber.d("updateCattle() failed : ${it.localizedMessage}")
                }
        }
    }

    @MainThread
    private fun deleteBreedingOfCattle(cattle: Cattle) {
        val performBatchDeleteOnSuccess : (QuerySnapshot) -> Unit = { result ->
            Timber.d("Deleting breeding data for cattle : ${cattle.id}")

            val resultChunks = result.chunked(BATCH_OPERATION_LIMIT)

            resultChunks.forEach { list ->
                firestore
                    .runBatch { writeBatch ->
                        list.forEach {
                            writeBatch.delete(it.reference)
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

        firestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(BREEDING_COLLECTION)
            .whereEqualTo(KEY_BREEDING_CATTLE_ID, cattle.id)
            .get()
            .addOnSuccessListener(performBatchDeleteOnSuccess)
            .addOnFailureListener {
                Timber.e(it, "Failed to get breeding data for cattle ${cattle.id}")
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
        private const val BATCH_OPERATION_LIMIT = 500

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