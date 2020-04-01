package com.pr656d.shared.data.signin

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo

open class FirebaseUserInfo (
    private val firebaseUser: FirebaseUser?
) : UserInfoBasic {

    override fun isSignedIn(): Boolean = firebaseUser != null

    override fun getEmail(): String? = firebaseUser?.email

    override fun getProviderData(): MutableList<out UserInfo>? = firebaseUser?.providerData

    override fun isAnonymous(): Boolean? = firebaseUser?.isAnonymous

    override fun getPhoneNumber(): String? = firebaseUser?.phoneNumber

    override fun getUid(): String? = firebaseUser?.uid

    override fun isEmailVerified(): Boolean? = firebaseUser?.isEmailVerified

    override fun getDisplayName(): String? = firebaseUser?.displayName

    override fun getPhotoUrl(): Uri? = firebaseUser?.photoUrl

    override fun getProviderId(): String? = firebaseUser?.providerId

    override fun getLastSignInTimestamp(): Long? = firebaseUser?.metadata?.lastSignInTimestamp

    override fun getCreationTimestamp(): Long? = firebaseUser?.metadata?.creationTimestamp
}