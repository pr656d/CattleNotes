package com.pr656d.shared.data.login.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pr656d.shared.data.db.AppDatabaseDao
import com.pr656d.shared.data.db.updater.DbUpdater
import com.pr656d.shared.data.user.info.FirebaseUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.fcm.FcmTokenUpdater
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthStateUserDataSource @Inject constructor(
    val firebase: FirebaseAuth,
    private val appDatabaseDao: AppDatabaseDao,
    private val dbUpdater: DbUpdater,
    private val tokenUpdater: FcmTokenUpdater
) : AuthStateUserDataSource {

    private val currentFirebaseUserObservable = MutableLiveData<Result<UserInfoBasic?>>()

    private var isAlreadyListening = false

    private var lastUid: String? = null

    private lateinit var auth: FirebaseAuth

    // Listener that saves the [FirebaseUser], fetches the ID token
    // and updates the user ID observable.
    private val authStateListener: ((FirebaseAuth) -> Unit) = {
        // Initialize auth as global
        auth = it

        DefaultScheduler.execute {
            Timber.d("Received a FirebaseAuth update.")
            // Post the current user for observers
            currentFirebaseUserObservable.postValue(
                Result.Success(
                    FirebaseUserInfo(auth.currentUser)
                )
            )

            auth.currentUser?.let { currentUser ->
                // Save the FCM ID token in firestore
                tokenUpdater.updateTokenForUser(currentUser.uid)
            }
        }

        // Log out
        if (auth.currentUser == null) {
            dbUpdater.stop()
            DefaultScheduler.execute {
                appDatabaseDao.clearDatabase()
            }
            // notificationAlarmUpdater.cancelAll()
        }

        // Log in
        auth.currentUser?.let {
            if (lastUid != auth.uid) { // Prevent duplicates
                dbUpdater.initialize()
                // notificationAlarmUpdater.updateAll(it.uid)
            }
        }
        // Save the last UID to prevent setting too many alarms.
        lastUid = auth.uid
    }

    override fun startListening() {
        if (!isAlreadyListening) {
            firebase.addAuthStateListener(authStateListener)
            isAlreadyListening = true
        }
    }

    override fun getBasicUserInfo(): LiveData<Result<UserInfoBasic?>> {
        return currentFirebaseUserObservable
    }

    override fun clearListener() {
        firebase.removeAuthStateListener(authStateListener)
    }

    override fun reload() {
        auth.currentUser
            ?.reload()
            ?.addOnSuccessListener {
                currentFirebaseUserObservable.postValue(
                    Result.Success(
                        FirebaseUserInfo(auth.currentUser)
                    )
                )
            }
            ?.addOnFailureListener {
                Timber.d("Firebase reload failed")
            }
    }
}