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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.pr656d.shared.data.login.datasources.ReloadFirebaseUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.result.Result
import timber.log.Timber
import javax.inject.Inject

class UpdateUserInfoBasicDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val reloadFirebaseUserInfo: ReloadFirebaseUserInfo
) : UpdateUserInfoBasicDataSource {

    private val result = MutableLiveData<Result<Unit>>()

    override fun updateUserInfo(userInfo: UserInfoBasic) {
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            Timber.d("Exception user id not found on update of UserInfoBasic")
            result.postValue(
                Result.Error(IllegalStateException("User id not found"))
            )
            return
        }

        val requestBuilder = UserProfileChangeRequest.Builder()

        // For now we are handling update request of display name and photo url.
        if (currentUser.displayName != userInfo.getDisplayName())
            requestBuilder.setDisplayName(userInfo.getDisplayName())

        if (currentUser.photoUrl != userInfo.getPhotoUrl())
            requestBuilder.setPhotoUri(userInfo.getPhotoUrl())

        currentUser
            .updateProfile(requestBuilder.build())
            .addOnSuccessListener {
                result.postValue(Result.Success(Unit))
                /**
                 * Firebase user info is not supporting realtime updates.
                 * We have to tell it about by reload call.
                 */
                reloadFirebaseUserInfo.reload()
            }
            .addOnFailureListener {
                Timber.d("Exception on update of UserInfoBasic")
                result.postValue(Result.Error(it))
            }
    }

    override fun observeUpdateResult(): LiveData<Result<Unit>> {
        return result
    }
}