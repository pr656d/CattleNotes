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

package com.pr656d.shared.data.user.info

import android.net.Uri
import com.google.firebase.auth.UserInfo

/**
 *  Interface to decouple the user info from firebase.
 */
interface UserInfoDetailed : UserInfoBasic,
    FirestoreUserInfo

interface UserInfoBasic {

    fun isSignedIn(): Boolean

    fun getEmail(): String?

    fun getProviderData(): MutableList<out UserInfo>?

    fun getLastSignInTimestamp(): Long?

    fun getCreationTimestamp(): Long?

    fun isAnonymous(): Boolean?

    fun getPhoneNumber(): String?

    fun getUid(): String?

    fun isEmailVerified(): Boolean?

    fun getDisplayName(): String?

    fun getPhotoUrl(): Uri?

    fun getProviderId(): String?

}

interface FirestoreUserInfo {
    fun getFarmName(): String?

    fun getFarmAddress(): String?

    fun getGender(): String?

    fun getDateOfBirth(): String?

    fun getAddress(): String?

    fun getDairyCode(): String?

    fun getDairyCustomerId(): String?
}