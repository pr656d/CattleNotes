package com.pr656d.shared.data.user.repository

import androidx.lifecycle.LiveData
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.info.datasources.UpdateUserInfoDetailedDataSource
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Single point of access for [UserInfoDetailed] data for the presentation layer.
 */
interface UserInfoRepository {

    fun updateUserInfo(userInfo: UserInfoDetailed)

    fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>>
}

@Singleton
open class UserInfoDataRepository @Inject constructor(
    private val updateUserInfoDetailedDataSource: UpdateUserInfoDetailedDataSource
) : UserInfoRepository {

    override fun updateUserInfo(userInfo: UserInfoDetailed) {
        updateUserInfoDetailedDataSource.updateUserInfo(userInfo)
    }

    override fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>> {
        return updateUserInfoDetailedDataSource.observeUpdateResult()
    }
}