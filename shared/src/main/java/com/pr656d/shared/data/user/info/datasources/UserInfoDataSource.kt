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

package com.pr656d.shared.data.user.info.datasources

import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import kotlinx.coroutines.flow.Flow

/**
 * Observes user changes made on firestore.
 *
 * @see ObserveFirestoreUserInfoDataSourceImpl
 */
interface ObserveFirestoreUserInfoDataSource {
    /**
     * Returns the holder of the result of listening to the data source.
     */
    fun getFirebaseUserInfo(): Flow<FirestoreUserInfo?>
}

/**
 * Updates [UserInfoDetailed]
 *
 * Refer [UserInfoDetailed] to see user info data holder.
 *
 * @see UpdateUserInfoDetailedDataSourceImpl
 */
interface UpdateUserInfoDetailedDataSource {
    /**
     * Handle user info update for
     * [UpdateUserInfoBasicDataSource] and [UpdateFirestoreUserInfoDataSource].
     *
     * @return Event of results
     *
     * @see UpdateUserInfoDetailedDataSourceImpl
     */
    suspend fun updateUserInfo(userInfo: UserInfoDetailed) : Event<Pair<Result<Unit>, Result<Unit>>>
}

/**
 * Updates [UserInfoBasic]
 *
 * @see UpdateUserInfoBasicDataSourceImpl
 */
interface UpdateUserInfoBasicDataSource {
    /**
     * @param userInfo of type [UserInfoBasic]
     * @return Result Success or Error.
     */
    suspend fun updateUserInfo(userInfo: UserInfoBasic) : Result<Unit>
}

/**
 * Updates [FirestoreUserInfo]
 *
 * @see UpdateFirestoreUserInfoDataSourceImpl
 */
interface UpdateFirestoreUserInfoDataSource {
    /**
     * @param userInfo of type [FirestoreUserInfo]
     * @return Result Success or Error.
     */
    suspend fun updateUserInfo(userInfo: FirestoreUserInfo) : Result<Unit>
}