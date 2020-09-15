/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.domain.user.info

import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.domain.SuspendUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

open class UpdateUserInfoDetailedUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<UserInfoDetailed, Event<Pair<Result<Unit>, Result<Unit>>>>(ioDispatcher) {

    override suspend fun execute(
        parameters: UserInfoDetailed
    ): Event<Pair<Result<Unit>, Result<Unit>>> = userInfoRepository.updateUserInfo(parameters)
}
