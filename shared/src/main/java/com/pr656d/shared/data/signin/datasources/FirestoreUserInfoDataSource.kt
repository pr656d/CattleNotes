package com.pr656d.shared.data.signin.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.pr656d.shared.data.signin.FirestoreUserInfo
import com.pr656d.shared.data.signin.UserInfoOnFirestore
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import timber.log.Timber
import javax.inject.Inject

class FirestoreUserInfoDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserInfoDataSource {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    private var registeredChangedListenerSubscription: ListenerRegistration? = null

    // Result can contain a null value (not processed) or a null result (not available).
    private val result = MutableLiveData<Result<UserInfoOnFirestore?>?>()

    // Keeping the last observed user ID, to avoid unnecessary calls
    private var lastUserId: String? = null

    override fun listenToUserChanges(userId: String) {
        val newUserId = if (lastUserId != userId) {
            userId
        } else {
            // User id update not needed.
            return
        }

        // Remove previous subscriptions, if exists.
        registeredChangedListenerSubscription?.remove()

        result.postValue(null)  // Reset result

        val registeredChangedListener =
            { snapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                DefaultScheduler.execute {
                    if (snapshot == null || !snapshot.exists()) {
                        // When the account signs in for the first time the document doesn't exist.
                        Timber.d("Document for snapshot $newUserId doesn't exist")
                        result.postValue(Result.Success(null))
                        return@execute
                    }

                    val userInfo =
                        FirestoreUserInfo(snapshot)

                    // Only emit a value if it's a new value or a value change.
                    if (result.value == null ||
                        (result.value as? Result.Success)?.data != userInfo
                    ) {
                        Timber.d("Received firestore user info")
                        result.postValue(Result.Success(userInfo))
                    }
                }
            }
        registeredChangedListenerSubscription = firestore
            .collection(USERS_COLLECTION)
            .document(newUserId)
            .addSnapshotListener(registeredChangedListener)
        lastUserId = newUserId
    }

    override fun observeResult(): LiveData<Result<UserInfoOnFirestore?>?> {
        return result
    }

    override fun removeUser() {
        registeredChangedListenerSubscription?.remove()
        lastUserId = null
        result.postValue(Result.Success(null))
    }
}