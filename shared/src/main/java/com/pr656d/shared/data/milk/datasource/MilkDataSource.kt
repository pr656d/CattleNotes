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
package com.pr656d.shared.data.milk.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Milk
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.di.MainDispatcher
import com.pr656d.shared.utils.FirestoreUtil.BATCH_OPERATION_LIMIT
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.toEpochMilli
import com.pr656d.shared.utils.toMilkOf
import com.pr656d.shared.utils.toMilkSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Remote data source for [Milk].
 * @see FirestoreMilkDataSource
 */
interface MilkDataSource {
    /**
     * Load milk list from data source.
     */
    suspend fun load(): List<Milk>

    /**
     * Add milk at remote data source.
     */
    suspend fun addMilk(milk: Milk)

    /**
     * Add milk at remote data source.
     */
    suspend fun addAllMilk(milkList: List<Milk>)

    /**
     * Update milk at remote data source.
     */
    suspend fun updateMilk(milk: Milk)

    /**
     * Delete milk at remote data source.
     */
    suspend fun deleteMilk(milk: Milk)
}

class FirestoreMilkDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : MilkDataSource {
    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at milk data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override suspend fun load(): List<Milk> = withContext(mainDispatcher) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        suspendCancellableCoroutine<List<Milk>> { continuation ->
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!continuation.isActive) return@addOnSuccessListener

                    val list = snapshot.documents.map { getMilk(it) }
                    continuation.resume(list)
                }
                .addOnFailureListener {
                    Timber.d("load() failed at milk data source : ${it.message}")
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun addMilk(milk: Milk) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .document(milk.id)
                .set(milk.asHashMap())
                .addOnSuccessListener {
                    Timber.d("Milk : ${milk.id} added successfully")
                }
                .addOnFailureListener {
                    Timber.d("addMilk() failed : ${it.message}")
                }
        }
    }

    override suspend fun addAllMilk(milkList: List<Milk>) {
        val chunks = milkList.chunked(BATCH_OPERATION_LIMIT)

        chunks.forEach { chunk ->
            firestore
                .runBatch { batch ->
                    chunk.forEach { milk ->
                        val docRef = firestore
                            .collection(USERS_COLLECTION)
                            .document(userId)
                            .collection(MILK_COLLECTION)
                            .document(milk.id)

                        batch.set(docRef, milk.asHashMap(), SetOptions.merge())
                    }
                }
                .addOnSuccessListener {
                    Timber.d("All ${milkList.count()} added successfully")
                }
                .addOnFailureListener {
                    Timber.e(it, "addAllMilk() failed : ${it.message}")
                }
        }
    }

    override suspend fun deleteMilk(milk: Milk) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        withContext(mainDispatcher) {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .document(milk.id)
                .delete()
                .addOnSuccessListener {
                    Timber.d("Milk : ${milk.id} deleted successfully")
                }
                .addOnFailureListener {
                    Timber.d("deleteMilk() failed : ${it.message}")
                }
        }
    }

    override suspend fun updateMilk(milk: Milk) {
        withContext(mainDispatcher) {
            // All Firestore operations start from the main thread to avoid concurrency issues.
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .document(milk.id)
                .set(milk.asHashMap(), SetOptions.merge())
                .addOnFailureListener {
                    Timber.d("updateMilk() failed : ${it.message}")
                }
        }
    }

    private fun Milk.asHashMap(): HashMap<String, Any?> = hashMapOf<String, Any?>().apply {
        put(KEY_SOURCE, source.SENDER_ADDRESS)
        put(KEY_TIMESTAMP, timestamp.toEpochMilli())
        put(KEY_MILK_OF, milkOf.displayName)
        put(KEY_QUANTITY, quantity.toString())
        put(KEY_FAT, fat.toString())
        put(KEY_AMOUNT, amount?.toString())
        put(KEY_TOTAL_QUANTITY, totalQuantity?.toString())
        put(KEY_TOTAL_AMOUNT, totalAmount?.toString())
        put(KEY_LINK, link)
    }

    private fun getMilk(document: DocumentSnapshot): Milk {
        return document.run {
            Milk(
                source = (get(KEY_SOURCE) as String).toMilkSource(),
                timestamp = (get(KEY_TIMESTAMP) as Long).let {
                    TimeUtils.toZonedDateTime(it)
                },
                milkOf = (get(KEY_MILK_OF) as String).toMilkOf(),
                quantity = (get(KEY_QUANTITY) as String).toFloat(),
                fat = (get(KEY_FAT) as String).toFloat(),
                amount = (get(KEY_AMOUNT) as? String)?.toFloat(),
                totalQuantity = (get(KEY_TOTAL_QUANTITY) as? String)?.toFloat(),
                totalAmount = (get(KEY_TOTAL_AMOUNT) as? String)?.toFloat(),
                link = get(KEY_LINK) as? String
            ).apply { id = this@run.id }
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val MILK_COLLECTION = "milkList"
        private const val KEY_SOURCE = "source"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_SHIFT = "shift"
        private const val KEY_MILK_OF = "milkOf"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_FAT = "fat"
        private const val KEY_AMOUNT = "amount"
        private const val KEY_TOTAL_QUANTITY = "totalQuantity"
        private const val KEY_TOTAL_AMOUNT = "totalAmount"
        private const val KEY_LINK = "link"
    }
}
