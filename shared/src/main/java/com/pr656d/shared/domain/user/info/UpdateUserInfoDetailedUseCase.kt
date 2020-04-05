package com.pr656d.shared.domain.user.info

import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

open class UpdateUserInfoDetailedUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) : MediatorUseCase<UserInfoDetailed, Pair<Result<Unit>, Result<Unit>>>() {

    init {
        result.addSource(userInfoRepository.observeUpdateResult()) {
            result.postValue(it)
        }
    }

    override fun execute(parameters: UserInfoDetailed) {
        userInfoRepository.updateUserInfo(parameters)
    }
}