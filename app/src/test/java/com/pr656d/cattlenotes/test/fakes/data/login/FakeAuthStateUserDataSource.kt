/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.test.fakes.data.login

import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.user.info.UserInfoBasic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthStateUserDataSource(
    private val user: UserInfoBasic?
) : AuthStateUserDataSource {
    override fun getBasicUserInfo(): Flow<UserInfoBasic?> = flow {
        emit(user)
    }

    override fun reload() {}
}