package com.pr656d.shared.data.user.info.datasources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class UpdateUserInfoDetailedDataSourceImpl @Inject constructor(
    private val updateUserInfoBasicDataSource: UpdateUserInfoBasicDataSource,
    private val updateFirestoreUserInfoDataSource: UpdateFirestoreUserInfoDataSource
) : UpdateUserInfoDetailedDataSource {

    private val result = MediatorLiveData<Result<Unit>>()

    init {
        val userInfoBasicResult = updateUserInfoBasicDataSource.observeUpdateResult()
        result.addSource(userInfoBasicResult) {
            result.postValue(it)
        }

        val userInfoOnFirestoreResult = updateFirestoreUserInfoDataSource.observeUpdateResult()
        result.addSource(userInfoOnFirestoreResult) {
            result.postValue(it)
        }
    }

    override fun updateUserInfo(userInfo: UserInfoDetailed) {
        updateUserInfoBasicDataSource.updateUserInfo(userInfo)
        updateFirestoreUserInfoDataSource.updateUserInfo(userInfo)
    }

    override fun observeUpdateResult(): LiveData<Result<Unit>> {
        return result
    }
}