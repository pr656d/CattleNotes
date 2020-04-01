package com.pr656d.shared.domain.auth

import com.pr656d.shared.data.signin.FirebaseUserInfoDetailed
import com.pr656d.shared.data.signin.UserInfoDetailed
import com.pr656d.shared.data.signin.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.signin.datasources.UserInfoDataSource
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [AuthStateUserDataSource] provides general user information, like user IDs.
 *
 * In future we can fetch from other data sources as well.
 */
@Singleton
open class ObserveUserAuthStateUseCase @Inject constructor(
    private val authStateUserDataSource: AuthStateUserDataSource,
    private val firestoreUserInfoDataSource: UserInfoDataSource
) : MediatorUseCase<Any, UserInfoDetailed?>() {

    private val currentFirebaseUserObservable =
        authStateUserDataSource.getBasicUserInfo()

    private val firestoreUserInfoObservable =
        firestoreUserInfoDataSource.observeResult()

    init {
        // If the Firebase user changes, query firestore to figure out if they're registered.
        result.addSource(currentFirebaseUserObservable) { userResult ->
            // Start observing the user in firestore to fetch more user detail on firestore.
            (userResult as? Result.Success)?.data?.getUid()?.let {
                firestoreUserInfoDataSource.listenToUserChanges(it)
            }

            // Sign out
            if (userResult is Result.Success && userResult.data?.isSignedIn() == false) {
                firestoreUserInfoDataSource.removeUser()
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
            // If the firestoreUserInfo is an error, assign it false. Null means no info yet.
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
            Timber.e("There was a registration error.")
            result.postValue(Result.Error(Exception("Registration error")))
        }
    }
}
