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

package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result

class FakeUserInfoRepository(
    private val updateResult: Pair<Result<Unit>, Result<Unit>> =
        Pair(Result.Success(Unit), Result.Success(Unit))
) : UserInfoRepository {
    private val result = MutableLiveData<Event<Pair<Result<Unit>, Result<Unit>>>>()

    override fun updateUserInfo(userInfo: UserInfoDetailed) {
        result.postValue(Event(updateResult))
    }

    override fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>> {
        return result
    }
}