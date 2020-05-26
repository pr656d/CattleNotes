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

package com.pr656d.shared.data.user.info.datasources

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ObserveFirestoreUserInfoDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authIdDataSource: AuthIdDataSource
) : ObserveFirestoreUserInfoDataSource {

    // Channel that keeps track of Firestore user info.
    private val channel = ConflatedBroadcastChannel<FirestoreUserInfo?>()

    private var userInfoChangedListenerSubscription: ListenerRegistration? = null

    @FlowPreview
    override fun getFirebaseUserInfo(): Flow<FirestoreUserInfo?> {
        // Remove previous subscriptions, if exists.
        userInfoChangedListenerSubscription?.remove()

        val userId = authIdDataSource.getUserId()

        if (userId == null) {
            Timber.d("User id not found")
            channel.offer(null)
        } else {
            val userInfoChangedListener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit =
                { snapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                    if (snapshot == null || !snapshot.exists()) {
                        // When the account signs in for the first time the document doesn't exist.
                        Timber.d("Document for snapshot $userId doesn't exist")
                    }

                    snapshot?.let {
                        val userInfo = getFirestoreUserInfo(snapshot)

                        if (!channel.isClosedForSend) {
                            channel.offer(userInfo)
                        } else {
                            removeUser()
                        }
                    }
                }

            userInfoChangedListenerSubscription = firestore
                .collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener(userInfoChangedListener)
        }

        return channel.asFlow().distinctUntilChanged()
    }

    private fun removeUser() {
        userInfoChangedListenerSubscription?.remove()
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
}