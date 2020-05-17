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

import androidx.lifecycle.LiveData
import com.pr656d.shared.data.user.info.FirestoreUserInfo
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result

/**
 * Observes user changes made on firestore.
 */
interface ObserveFirestoreUserInfoDataSource {
    /**
     * Listens to changes in the user document in Firestore. A Change in the "users/userId" fields
     * will emit a new user.
     */
    fun listenToUserChanges(userId: String)

    /**
     * Returns the holder of the result of listening to the data source.
     */
    fun observeFirestoreUserInfo(): LiveData<Result<FirestoreUserInfo?>?>

    /**
     * Clear listeners and set the result of the observable to false when the user is not signed in.
     */
    fun removeUser()
}

/**
 * Updates [UserInfoDetailed]
 *
 * Refer [UserInfoDetailed] to see user info data holder.
 */
interface UpdateUserInfoDetailedDataSource {

    /**
     * Handle user info update for
     * [UpdateUserInfoBasicDataSource] and [UpdateFirestoreUserInfoDataSource].
     *
     * @see userInfo of type [UserInfoDetailed]
     */
    fun updateUserInfo(userInfo: UserInfoDetailed)

    /**
     * Returns holder of the result of update user info. It does updates to two different sources.
     * Holds their result with pair. Return result as Event. Handle it once only as data sources are
     * provided as singleton.
     */
    fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>>

}

/**
 * Updates [UserInfoBasic]
 */
interface UpdateUserInfoBasicDataSource {

    /**
     * @param userInfo of type [UserInfoBasic]
     */
    fun updateUserInfo(userInfo: UserInfoBasic)

    /**
     * Observe result of update.
     */
    fun observeUpdateResult(): LiveData<Result<Unit>>

}

/**
 * Updates [FirestoreUserInfo]
 */
interface UpdateFirestoreUserInfoDataSource {

    /**
     * @param userInfo of type [FirestoreUserInfo]
     */
    fun updateUserInfo(userInfo: FirestoreUserInfo)

    /**
     * Observe result of update.
     */
    fun observeUpdateResult(): LiveData<Result<Unit>>

}