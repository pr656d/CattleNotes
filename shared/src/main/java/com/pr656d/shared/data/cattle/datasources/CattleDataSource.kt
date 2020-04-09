package com.pr656d.shared.data.cattle.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.model.Cattle
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.TimeUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for [Cattle].
 */
interface CattleDataSource {
    fun addCattle(cattle: Cattle)
    fun deleteCattle(cattle: Cattle)
    fun updateCattle(cattle: Cattle)
}

@Singleton
class FirestoreCattleDataSource @Inject constructor(
    authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore
) : CattleDataSource {
    private val userId by lazy {
        authIdDataSource.getUserId() ?: run {
            Timber.e("User Id not found at cattle data source")
            throw IllegalStateException("User Id not found")
        }
    }

    override fun addCattle(cattle: Cattle) {
        // All Firestore operations start from the main thread to avoid concurrency issues.
        DefaultScheduler.postToMainThread {
            firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .collection(CATTLE_COLLECTION)
                .document()
                .set(cattle.asHashMap())
                .addOnFailureListener {
                    Timber.e("addCattle() failed : ${it.localizedMessage}")
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
                .addOnFailureListener {
                    Timber.e("deleteCattle() failed : ${it.localizedMessage}")
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
                    Timber.e("updateCattle() failed : ${it.localizedMessage}")
                }
        }
    }

    private fun Cattle.asHashMap(): HashMap<String, Any?> = hashMapOf<String, Any?>().apply {
        put(KEY_TAG_NUMBER, tagNumber)
        put(KEY_NAME, name)
        put(KEY_IMAGE_URL, image?.remotePath)
        put(KEY_TYPE, type.displayName)
        put(KEY_BREED, breed.displayName)
        put(KEY_GROUP, group.displayName)
        put(KEY_LACTATION, lactation)
        put(KEY_HOME_BORN, homeBorn)
        put(KEY_PURCHASE_AMOUNT, purchaseAmount)
        put(KEY_PURCHASE_Date, purchaseDate?.let { TimeUtils.toEpochMillis(it) } )
        put(KEY_DOB, dateOfBirth?.let { TimeUtils.toEpochMillis(it) })
        put(KEY_PARENT, parent)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val CATTLE_COLLECTION = "cattleList"
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