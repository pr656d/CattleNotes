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
package com.pr656d.shared.data.user.repository

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
    suspend fun updateUserInfo(userInfo: UserInfoDetailed): Event<Pair<Result<Unit>, Result<Unit>>>
}

@Singleton
open class UserInfoDataRepository @Inject constructor(
    private val updateUserInfoDetailedDataSource: UpdateUserInfoDetailedDataSource
) : UserInfoRepository {

    override suspend fun updateUserInfo(
        userInfo: UserInfoDetailed
    ): Event<Pair<Result<Unit>, Result<Unit>>> = updateUserInfoDetailedDataSource.updateUserInfo(
        userInfo
    )
}
