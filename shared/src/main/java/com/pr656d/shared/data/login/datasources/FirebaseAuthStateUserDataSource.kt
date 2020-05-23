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

import com.google.firebase.auth.FirebaseAuth
import com.pr656d.shared.data.db.dao.AppDatabaseDao
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.user.info.FirebaseUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.fcm.FcmTokenUpdater
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseAuthStateUserDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    appDatabaseDao: AppDatabaseDao,
    tokenUpdater: FcmTokenUpdater,
    preferenceStorageRepository: PreferenceStorageRepository,
    dbLoader: DbLoader,
    breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater,
    @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
) : AuthStateUserDataSource {

    private val logoutScope = CoroutineScope(coroutineDispatcher + Job())

    // Channel that keeps track of User Authentication
    private val channel = ConflatedBroadcastChannel<UserInfoBasic?>()

    private var isListening = false
    private var lastUid: String? = null

    // Save auth as global.
    private lateinit var auth: FirebaseAuth

    // Listener that saves the [FirebaseUser], fetches the ID token
    // and updates the user ID observable.
    private val authStateListener: ((FirebaseAuth) -> Unit) = { auth ->
        /** Save the latest copy of auth */
        this.auth = auth

        // Log in
        auth.currentUser?.let {
            // Save the FCM ID token in firestore
            tokenUpdater.updateTokenForUser(it.uid)

            if (lastUid != auth.uid) { // Prevent duplicates
                dbLoader.initialize()
                // Update all the breeding alarms.
                breedingNotificationAlarmUpdater.updateAll(it.uid)
            }
        }

        // Log out
        if (auth.currentUser == null) {
            // Launch new coroutine.
            logoutScope.launch {
                // Cancel all the breeding alarms.
                breedingNotificationAlarmUpdater.cancelAll()
                appDatabaseDao.clear()
                preferenceStorageRepository.clear()
            }
        }

        // Save the last UID to prevent setting too many alarms.
        lastUid = this.auth.uid

        // Post the current user for observers
        if (!channel.isClosedForSend) {
            channel.offer(FirebaseUserInfo(auth.currentUser))
        } else {
            unregisterListener()
        }
    }

    // Synchronized method, multiple calls to this method at the same time isn't allowed since
    // isListening is read and can be modified
    @FlowPreview
    @Synchronized
    override fun getBasicUserInfo(): Flow<UserInfoBasic?> {
        if (!isListening) {
            firebaseAuth.addAuthStateListener(authStateListener)
            isListening = true
        }
        return channel.asFlow()
    }

    private fun unregisterListener() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    override fun reload() {
        auth.currentUser
            ?.reload()
            ?.addOnSuccessListener {
                if (!channel.isClosedForSend) {
                    channel.offer(FirebaseUserInfo(auth.currentUser))
                }
            }
            ?.addOnFailureListener {
                Timber.d("Firebase reload failed")
            }
    }
}