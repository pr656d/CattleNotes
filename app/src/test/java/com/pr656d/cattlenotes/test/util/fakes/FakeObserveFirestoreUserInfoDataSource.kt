package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSource
import com.pr656d.shared.domain.result.Result

class FakeObserveFirestoreUserInfoDataSource(
    private val firestoreUserInfo: Result<FirestoreUserInfo?>?
) : ObserveFirestoreUserInfoDataSource {
    override fun listenToUserChanges(userId: String) {}

    override fun observeFirestoreUserInfo(): LiveData<Result<FirestoreUserInfo?>?> {
        return MutableLiveData<Result<FirestoreUserInfo?>>()
            .apply { value = firestoreUserInfo }
    }

    override fun removeUser() {}
}