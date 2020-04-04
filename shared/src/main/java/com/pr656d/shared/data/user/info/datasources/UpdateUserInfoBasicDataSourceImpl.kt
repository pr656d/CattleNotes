package com.pr656d.shared.data.user.info.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.result.Result
import timber.log.Timber
import javax.inject.Inject

class UpdateUserInfoBasicDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
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

        val oldUserName = currentUser.displayName
        val newUserName = userInfo.getDisplayName()

        // For now we are handling update request of display name and photo url.
        if (currentUser.displayName != userInfo.getDisplayName())
            requestBuilder.setDisplayName(userInfo.getDisplayName())

        if (currentUser.photoUrl != userInfo.getPhotoUrl())
            requestBuilder.setPhotoUri(userInfo.getPhotoUrl())

        currentUser
            .updateProfile(requestBuilder.build())
            .addOnSuccessListener {
                result.postValue(Result.Success(Unit))
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