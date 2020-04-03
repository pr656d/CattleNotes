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