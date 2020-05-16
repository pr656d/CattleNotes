package com.pr656d.shared.data.milk.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Milk
import com.pr656d.shared.data.db.MilkDao
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.toEpochMilli
import com.pr656d.shared.utils.toMilkOf
import com.pr656d.shared.utils.toMilkSource
import timber.log.Timber
import javax.inject.Inject

/**
 * Remote data source for [Milk].
 * @see FirestoreMilkDataSource
 */
interface MilkDataSource {
    /**
     * Load milk list from data source and save to local db.
     */
    fun load(onComplete: () -> Unit = {})

    /**
     * Add milk at remote data source.
     */
    fun addMilk(milk: Milk)

    /**
     * Add milk at remote data source.
     */
    fun addAllMilk(milkList: List<Milk>)

    /**
     * Delete milk at remote data source.
     */
    fun deleteMilk(milk: Milk)

    /**
     * Update milk at remote data source.
     */
    fun updateMilk(milk : Milk)
}

class FirestoreMilkDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    private val milkDao: MilkDao
) : MilkDataSource {
    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at milk data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override fun load(onComplete: () -> Unit) {
        val onSuccessListener: (QuerySnapshot) -> Unit = { snapshot ->
            DefaultScheduler.execute {
                val milkList = snapshot.documents.map { getMilk(it) }
                milkDao.insertAll(milkList).also {
                    onComplete()
                }
            }
        }

        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener {
                    Timber.d("load() failed at milk data source : ${it.message}")
                }
        }
    }

    override fun addMilk(milk: Milk) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .document(milk.id)
                .set(milk.asHashMap())
                .addOnFailureListener {
                    Timber.d("addMilk() failed : ${it.message}")
                }
        }
    }

    override fun addAllMilk(milkList: List<Milk>) {
        /**
         * Firestore has batch operation limit.
         * Make chunks of list and perform batch commit.
         */
        val milkListBlocks = milkList.chunked(BATCH_OPERATION_LIMIT)

        milkListBlocks.forEach { list ->
            firestore
                .runBatch { batch ->
                    list.forEach { milk ->
                        val docRef = firestore
                            .collection(USERS_COLLECTION)
                            .document(userId)
                            .collection(MILK_COLLECTION)
                            .document(milk.id)

                        batch.set(docRef, milk.asHashMap())
                    }
                }
                .addOnSuccessListener {
                    Timber.d("addAllMilk() success.")
                }
                .addOnFailureListener {
                    Timber.e(it, "addAllMilk() failed : ${it.message}")
                }
        }
    }

    override fun deleteMilk(milk: Milk) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(MILK_COLLECTION)
                .document(milk.id)
                .delete()
                .addOnFailureListener {
                    Timber.d("deleteMilk() failed : ${it.message}")
                }
        }
    }

    override fun updateMilk(milk: Milk) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
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
        private const val BATCH_OPERATION_LIMIT = 500

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