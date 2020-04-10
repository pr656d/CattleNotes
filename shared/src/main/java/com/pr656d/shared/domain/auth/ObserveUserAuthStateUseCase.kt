package com.pr656d.shared.domain.auth

import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.user.info.FirebaseUserInfoDetailed
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSource
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [AuthStateUserDataSource] provides general user information,
 * like user ID, Name, Farm name, dairy code, etc.
 */
@Singleton
open class ObserveUserAuthStateUseCase @Inject constructor(
    private val authStateUserDataSource: AuthStateUserDataSource,
    private val observeFirestoreUserInfoDataSource: ObserveFirestoreUserInfoDataSource
) : MediatorUseCase<Any, UserInfoDetailed?>() {

    private val currentFirebaseUserObservable =
        authStateUserDataSource.getBasicUserInfo()

    private val firestoreUserInfoObservable =
        observeFirestoreUserInfoDataSource.observeFirestoreUserInfo()

    init {
        // If the Firebase user changes, query firestore to fetch user info on firestore.
        result.addSource(currentFirebaseUserObservable) { userResult ->
            // Start observing the user in firestore to fetch more user info from firestore.
            // Sign in
            (userResult as? Result.Success)?.data?.getUid()?.let {
                observeFirestoreUserInfoDataSource.listenToUserChanges(it)
            }

            // Sign out
            if (userResult is Result.Success && userResult.data?.isSignedIn() == false) {
                observeFirestoreUserInfoDataSource.removeUser()
                updateUserObservable()
            }

            // Error
            if (userResult is Result.Error) {
                result.postValue(Result.Error(Exception("FirebaseAuth error")))
            }
        }

        result.addSource(firestoreUserInfoObservable) {
            // When we get full detail of the user, update the user.
            updateUserObservable()
        }
    }

    override fun execute(parameters: Any) {
        // Start listening to the [AuthStateUserDataSource] for changes in auth state.
        authStateUserDataSource.startListening()
    }

    private fun updateUserObservable() {
        val currentFirebaseUser = currentFirebaseUserObservable.value
        val firestoreUserInfo = firestoreUserInfoObservable.value

        if (currentFirebaseUser is Result.Success) {
            // If the firestoreUserInfo is an error, assign it null. Null means no info yet.
            val firestoreUserInfoValue = (firestoreUserInfo as? Result.Success)?.data

            result.postValue(
                Result.Success(
                    FirebaseUserInfoDetailed(
                        currentFirebaseUser.data,
                        firestoreUserInfoValue
                    )
                )
            )
        } else {
            Timber.e("There was a user info on firebase error.")
            result.postValue(Result.Error(Exception("User info on firebase error")))
        }
    }
}
