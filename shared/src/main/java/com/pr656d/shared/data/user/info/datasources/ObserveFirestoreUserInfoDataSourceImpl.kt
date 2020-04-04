package com.pr656d.shared.data.user.info.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import timber.log.Timber
import javax.inject.Inject

class ObserveFirestoreUserInfoDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ObserveFirestoreUserInfoDataSource {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val FARM_NAME_KEY = "farmName"
        private const val FARM_ADDRESS_KEY = "farmAddress"
        private const val GENDER_KEY = "gender"
        private const val DOB_KEY = "dateOfBirth"
        private const val ADDRESS_KEY = "address"
        private const val DAIRY_CODE = "dairyCode"
        private const val DAIRY_CUSTOMER_ID = "dairyCustomerId"
    }

    private var userInfoChangedListenerSubscription: ListenerRegistration? = null

    // Result can contain a null value (not processed) or a null result (not available).
    private val result = MutableLiveData<Result<FirestoreUserInfo?>?>()

    // Keeping the last observed user ID, to avoid unnecessary calls
    private var lastUserId: String? = null

    // Holds reference of the snapshot
    private var documentReference: DocumentReference? = null

    override fun listenToUserChanges(userId: String) {
        val newUserId = if (lastUserId != userId) {
            userId
        } else {
            // User id update not needed.
            return
        }

        // Remove previous subscriptions, if exists.
        userInfoChangedListenerSubscription?.remove()

        result.postValue(null)  // Reset result

        val userInfoChangedListener =
            { snapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                // Update user info snapshot reference
                documentReference = snapshot?.reference

                DefaultScheduler.execute {
                    if (snapshot == null || !snapshot.exists()) {
                        // When the account signs in for the first time the document doesn't exist.
                        Timber.d("Document for snapshot $newUserId doesn't exist")
                        result.postValue(Result.Success(null))
                        return@execute
                    }

                    val userInfo = getFirestoreUserInfo(snapshot)

                    // Only emit a value if it's a new value or a value change.
                    if (result.value == null ||
                        (result.value as? Result.Success)?.data != userInfo
                    ) {
                        Timber.d("Received firestore user info")
                        result.postValue(Result.Success(userInfo))
                    }
                }
            }
        userInfoChangedListenerSubscription = firestore
            .collection(USERS_COLLECTION)
            .document(newUserId)
            .addSnapshotListener(userInfoChangedListener)
        lastUserId = newUserId
    }

    private fun getFirestoreUserInfo(snapshot: DocumentSnapshot): FirestoreUserInfo {
        return object : FirestoreUserInfo {
            override fun getFarmName(): String? {
                return snapshot[FARM_NAME_KEY] as? String
            }

            override fun getFarmAddress(): String? {
                return snapshot[FARM_ADDRESS_KEY] as? String
            }

            override fun getGender(): String? {
                return snapshot[GENDER_KEY] as? String
            }

            override fun getDateOfBirth(): String? {
                return snapshot[DOB_KEY] as? String
            }

            override fun getAddress(): String? {
                return snapshot[ADDRESS_KEY] as? String
            }

            override fun getDairyCode(): String? {
                return snapshot[DAIRY_CODE] as? String
            }

            override fun getDairyCustomerId(): String? {
                return snapshot[DAIRY_CUSTOMER_ID] as? String
            }
        }
    }

    override fun observeFirestoreUserInfo(): LiveData<Result<FirestoreUserInfo?>?> {
        return result
    }

    override fun removeUser() {
        userInfoChangedListenerSubscription?.remove()
        lastUserId = null
        result.postValue(Result.Success(null))
    }
}