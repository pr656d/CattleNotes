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
package com.pr656d.shared.domain.auth

import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.user.info.FirebaseUserInfoDetailed
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSource
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.FlowUseCase
import com.pr656d.shared.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [FlowUseCase] that observes two data sources to generate an [UserInfoDetailed]
 * that includes general user information, like user ID, Name, Farm name, dairy code, etc.
 */
@Singleton
open class ObserveUserAuthStateUseCase @Inject constructor(
    private val authStateUserDataSource: AuthStateUserDataSource,
    private val observeFirestoreUserInfoDataSource: ObserveFirestoreUserInfoDataSource,
    @DefaultDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Any, UserInfoDetailed?>(coroutineDispatcher) {

    override fun execute(parameters: Any): Flow<Result<UserInfoDetailed?>> {
        return authStateUserDataSource.getBasicUserInfo()
            .combineTransform(
                observeFirestoreUserInfoDataSource.getFirebaseUserInfo()
            ) { userInfoBasic, userInfoFirestore ->
                emit(
                    Result.Success(
                        FirebaseUserInfoDetailed(userInfoBasic, userInfoFirestore)
                    )
                )
            }
    }
}
