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
 * Implementation of [UserInfoDetailed]
 */
class FirebaseUserInfoDetailed (
    private val basicUserInfo: UserInfoBasic?,
    private val firestoreUserInfo: FirestoreUserInfo?
) : UserInfoDetailed {

    override fun isSignedIn(): Boolean = basicUserInfo?.isSignedIn() == true

    override fun getEmail(): String? = basicUserInfo?.getEmail()

    override fun getProviderData(): MutableList<out UserInfo>? = basicUserInfo?.getProviderData()

    override fun getLastSignInTimestamp(): Long? = basicUserInfo?.getLastSignInTimestamp()

    override fun getCreationTimestamp(): Long? = basicUserInfo?.getCreationTimestamp()

    override fun isAnonymous(): Boolean? = basicUserInfo?.isAnonymous()

    override fun getPhoneNumber(): String? = basicUserInfo?.getPhoneNumber()

    override fun getUid(): String? = basicUserInfo?.getUid()

    override fun isEmailVerified(): Boolean? = basicUserInfo?.isEmailVerified()

    override fun getDisplayName(): String? = basicUserInfo?.getDisplayName()

    override fun getPhotoUrl(): Uri? = basicUserInfo?.getPhotoUrl()

    override fun getProviderId(): String? = basicUserInfo?.getProviderId()

    override fun getFarmName(): String? = firestoreUserInfo?.getFarmName()

    override fun getFarmAddress(): String? = firestoreUserInfo?.getFarmAddress()

    override fun getGender(): String? = firestoreUserInfo?.getGender()

    override fun getDateOfBirth(): String? = firestoreUserInfo?.getDateOfBirth()

    override fun getAddress(): String? = firestoreUserInfo?.getAddress()

    override fun getDairyCode(): String? = firestoreUserInfo?.getDairyCode()

    override fun getDairyCustomerId(): String? = firestoreUserInfo?.getDairyCustomerId()

}