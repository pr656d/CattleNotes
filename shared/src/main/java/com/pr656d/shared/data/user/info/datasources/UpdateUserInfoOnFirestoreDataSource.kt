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

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.NetworkHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class UpdateFirestoreUserInfoDataSourceImpl @Inject constructor(
    private val authIdDataSource: AuthIdDataSource,
    private val firestore: FirebaseFirestore,
    private val networkHelper: NetworkHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UpdateFirestoreUserInfoDataSource {

    override suspend fun updateUserInfo(userInfo: FirestoreUserInfo): Result<Unit> =
        withContext(ioDispatcher) {
            suspendCancellableCoroutine<Result<Unit>> { continuation ->
                val uId = authIdDataSource.getUserId()

                // UId is required to work.
                if (uId == null) {
                    Timber.d("Exception on update of UserInfoOnFirestore")
                    continuation.resume(
                        Result.Error(IllegalStateException("User id not found"))
                    )
                } else {
                    val task = firestore
                        .collection(USERS_COLLECTION)
                        .document(uId)
                        .set(userInfo.asHashMap(), SetOptions.merge())

                    if (networkHelper.isNetworkConnected()) {
                        task
                            .addOnSuccessListener {
                                continuation.resume(Result.Success(Unit))
                            }
                            .addOnFailureListener {
                                Timber.d("Exception on update of UserInfoOnFirestore")
                                continuation.resume(Result.Error(it))
                            }
                    } else {
                        // Firebase offline persistence will reflect changes.
                        continuation.resume(Result.Success(Unit))
                    }
                }
            }
    }

    private fun FirestoreUserInfo.asHashMap(): HashMap<String, Any?> =
        hashMapOf<String, Any?>().apply {
            put(KEY_FARM_NAME, getFarmName())
            put(KEY_DAIRY_CODE, getDairyCode())
            put(KEY_DAIRY_CUSTOMER_ID, getDairyCustomerId())
            put(KEY_FARM_ADDRESS, getFarmAddress())
            put(KEY_GENDER, getGender())
            put(KEY_DOB, getDateOfBirth())
            put(KEY_ADDRESS, getAddress())
        }

    companion object {
        const val USERS_COLLECTION = "users"
        const val KEY_FARM_NAME = "farmName"
        const val KEY_FARM_ADDRESS = "farmAddress"
        const val KEY_GENDER = "gender"
        const val KEY_DOB = "dateOfBirth"
        const val KEY_ADDRESS = "address"
        const val KEY_DAIRY_CODE = "dairyCode"
        const val KEY_DAIRY_CUSTOMER_ID = "dairyCustomerId"
    }
}