package com.pr656d.shared.domain.user

import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.MediatorUseCase
import javax.inject.Inject

open class UpdateUserInfoDetailedUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) : MediatorUseCase<UserInfoDetailed, Unit>() {

    init {
        result.addSource(userInfoRepository.observeUpdateResult()) {
            result.postValue(it)
        }
    }

    override fun execute(parameters: UserInfoDetailed) {
        userInfoRepository.updateUserInfo(parameters)
    }
}