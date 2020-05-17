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

package com.pr656d.shared.data.login.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pr656d.shared.data.db.AppDatabaseDao
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.user.info.FirebaseUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.fcm.FcmTokenUpdater
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthStateUserDataSource @Inject constructor(
    private val firebase: FirebaseAuth,
    appDatabaseDao: AppDatabaseDao,
    tokenUpdater: FcmTokenUpdater,
    preferenceStorageRepository: PreferenceStorageRepository,
    dbLoader: DbLoader,
    breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater
) : AuthStateUserDataSource {

    private val currentFirebaseUserObservable = MutableLiveData<Result<UserInfoBasic?>>()

    private var isAlreadyListening = false

    private var lastUid: String? = null

    private lateinit var auth: FirebaseAuth

    // Listener that saves the [FirebaseUser], fetches the ID token
    // and updates the user ID observable.
    private val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
        // Initialize auth as global
        this.auth = auth

        DefaultScheduler.execute {
            Timber.d("Received a FirebaseAuth update.")
            // Post the current user for observers
            currentFirebaseUserObservable.postValue(
                Result.Success(
                    FirebaseUserInfo(this.auth.currentUser)
                )
            )

            this.auth.currentUser?.let { currentUser ->
                // Save the FCM ID token in firestore
                tokenUpdater.updateTokenForUser(currentUser.uid)
            }
        }

        // Log out
        if (this.auth.currentUser == null) {
            // Cancel all the breeding alarms.
            breedingNotificationAlarmUpdater.cancelAll(onComplete = {
                DefaultScheduler.execute {
                    // Wait until alarm cancellation completes before erasing data.
                    appDatabaseDao.clear()
                }
            })
            dbLoader.stop()
            preferenceStorageRepository.clear()
        }

        // Log in
        this.auth.currentUser?.let {
            if (lastUid != this.auth.uid) { // Prevent duplicates
                dbLoader.initialize()
                // Update all the breeding alarms.
                breedingNotificationAlarmUpdater.updateAll(it.uid)
            }
        }
        // Save the last UID to prevent setting too many alarms.
        lastUid = this.auth.uid
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