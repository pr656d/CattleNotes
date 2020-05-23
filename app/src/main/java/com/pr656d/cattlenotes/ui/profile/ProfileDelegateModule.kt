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

package com.pr656d.cattlenotes.ui.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.login.datasources.ObserveFirebaseUserInfoDataSource
import com.pr656d.shared.data.login.datasources.ReloadFirebaseUserInfo
import com.pr656d.shared.data.user.info.datasources.*
import com.pr656d.shared.data.user.repository.UserInfoDataRepository
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.domain.user.info.ObserveUserInfoDetailed
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@Suppress("UNUSED")
class ProfileDelegateModule {

    @Singleton
    @Provides
    fun provideUpdateUserInfoDetailedDataSource(
        updateUserInfoBasicDataSource: UpdateUserInfoBasicDataSource,
        updateFirestoreUserInfoDataSource: UpdateFirestoreUserInfoDataSource
    ): UpdateUserInfoDetailedDataSource = UpdateUserInfoDetailedDataSourceImpl(
        updateUserInfoBasicDataSource,
        updateFirestoreUserInfoDataSource
    )

    @Singleton
    @Provides
    fun provideUserInfoRepository(
        updateUserInfoDetailedDataSource: UpdateUserInfoDetailedDataSource
    ) : UserInfoRepository = UserInfoDataRepository(
        updateUserInfoDetailedDataSource
    )

    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideProfileDelegate(
        observeUserInfoDetailed: ObserveUserInfoDetailed,
        updateUserInfoDetailedUseCase: UpdateUserInfoDetailedUseCase,
        networkHelper: NetworkHelper
    ) : ProfileDelegate {
        return ProfileDelegateImp (
            observeUserInfoDetailed,
            updateUserInfoDetailedUseCase,
            networkHelper
        )
    }

    @Singleton
    @Provides
    fun provideUpdateFirestoreUserInfoDataSource(
        authIdDataSource: AuthIdDataSource,
        firestore: FirebaseFirestore,
        networkHelper: NetworkHelper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UpdateFirestoreUserInfoDataSource = UpdateFirestoreUserInfoDataSourceImpl(
        authIdDataSource, firestore, networkHelper, ioDispatcher
    )

    @Singleton
    @Provides
    fun provideReloadFirebaseUserInfo(
        observeFirebaseUserInfoDataSource: ObserveFirebaseUserInfoDataSource
    ) : ReloadFirebaseUserInfo = observeFirebaseUserInfoDataSource

    @Singleton
    @Provides
    fun provideUpdateUserInfoBasicDataSource(
        firebaseAuth: FirebaseAuth,
        reloadFirebaseUserInfo: ReloadFirebaseUserInfo,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UpdateUserInfoBasicDataSource = UpdateUserInfoBasicDataSourceImpl(
        firebaseAuth, reloadFirebaseUserInfo, ioDispatcher
    )
}