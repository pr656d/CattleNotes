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
package com.pr656d.shared.data.login.datasources

import com.pr656d.shared.data.user.info.UserInfoBasic
import kotlinx.coroutines.flow.Flow

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 * @see FirebaseAuthStateUserDataSource
 */
interface AuthStateUserDataSource : ReloadFirebaseUserInfo {
    /**
     * Returns an observable of the [UserInfoBasic].
     */
    fun getBasicUserInfo(): Flow<UserInfoBasic?>
}

/**
 * Force to reload firebase user info.
 * @see FirebaseAuthStateUserDataSource.reload
 */
interface ReloadFirebaseUserInfo {
    /**
     * Reloads the firebase user info.
     */
    fun reload()
}
