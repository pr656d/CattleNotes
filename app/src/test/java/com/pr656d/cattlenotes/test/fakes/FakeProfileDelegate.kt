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
package com.pr656d.cattlenotes.test.fakes

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.cattlenotes.test.fakes.data.user.FakeUserInfoRepository
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.cattlenotes.ui.profile.ProfileDelegateImp
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class FakeProfileDelegate(
    userInfoDetailed: UserInfoDetailed? = mock {
        on { isSignedIn() }.doReturn(true)
        on { getDisplayName() }.doReturn("Prem Patel")
        on { getEmail() }.doReturn("someone@something.com")
        on { getUid() }.doReturn(UUID.randomUUID().toString())
        on { getGender() }.doReturn("Male")
        on { getFarmName() }.doReturn("Some Name")
        on { getDairyCode() }.doReturn("1231")
        on { getDairyCustomerId() }.doReturn("9628276")
    },
    userInfoBasic: UserInfoBasic? = userInfoDetailed,
    firestoreUserInfo: FirestoreUserInfo? = userInfoDetailed,
    userInfoRepository: UserInfoRepository = FakeUserInfoRepository(),
    networkHelper: NetworkHelper = mock { on { isNetworkConnected() }.doReturn(true) },
    coroutineDispatcher: CoroutineDispatcher
) : ProfileDelegate by ProfileDelegateImp(
        FakeObserveUserAuthStateUseCase(userInfoBasic, firestoreUserInfo, coroutineDispatcher),
        UpdateUserInfoDetailedUseCase(userInfoRepository, coroutineDispatcher),
        networkHelper
    )
