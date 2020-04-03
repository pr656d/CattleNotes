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