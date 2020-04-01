/*
 * Copyright 2018 Google LLC
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

package com.pr656d.shared.data.signin.datasources

import androidx.lifecycle.LiveData
import com.pr656d.shared.data.signin.UserInfoBasic
import com.pr656d.shared.domain.result.Result

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 *
 * @see FirebaseAuthStateUserDataSource
 */
interface AuthStateUserDataSource {
    /**
     * Listens to changes in the authentication-related user info.
     */
    fun startListening()

    /**
     * Returns an observable of the [UserInfoBasic].
     */
    fun getBasicUserInfo(): LiveData<Result<UserInfoBasic?>>

    /**
     * Call this method to clear listeners to avoid leaks.
     */
    fun clearListener()
}
