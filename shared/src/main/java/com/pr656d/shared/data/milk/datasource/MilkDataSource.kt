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
import com.pr656d.shared.utils.toMilkOf
import com.pr656d.shared.utils.toMilkShift
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
        put(KEY_TIMESTAMP, timestamp)
        put(KEY_SHIFT, shift.displayName)
        put(KEY_MILK_OF, milkOf.displayName)
        put(KEY_QUANTITY, quantity)
        put(KEY_FAT, fat)
        put(KEY_AMOUNT, amount)
        put(KEY_TOTAL_QUANTITY, totalQuantity)
        put(KEY_TOTAL_AMOUNT, totalAmount)
        put(KEY_LINK, link)
    }

    private fun getMilk(document: DocumentSnapshot): Milk {
        return document.run {
            Milk(
                source = (get(KEY_SOURCE) as String).toMilkSource(),
                timestamp = (get(KEY_TIMESTAMP) as Long).let {
                    TimeUtils.toZonedDateTime(it)
                },
                shift = (get(KEY_SHIFT) as String).toMilkShift(),
                milkOf = (get(KEY_MILK_OF) as String).toMilkOf(),
                quantity = get(KEY_QUANTITY) as Float,
                fat = get(KEY_FAT) as Float,
                amount = get(KEY_AMOUNT) as Float,
                totalQuantity = get(KEY_TOTAL_QUANTITY) as Float,
                totalAmount = get(KEY_TOTAL_AMOUNT) as Float,
                link = get(KEY_LINK) as String
            )
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