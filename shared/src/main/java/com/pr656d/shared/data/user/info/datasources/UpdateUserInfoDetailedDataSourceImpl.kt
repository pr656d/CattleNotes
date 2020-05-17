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
import androidx.lifecycle.MediatorLiveData
import com.pr656d.shared.data.user.info.UserInfoDetailed
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class UpdateUserInfoDetailedDataSourceImpl @Inject constructor(
    private val updateUserInfoBasicDataSource: UpdateUserInfoBasicDataSource,
    private val updateFirestoreUserInfoDataSource: UpdateFirestoreUserInfoDataSource
) : UpdateUserInfoDetailedDataSource {

    private val result = MediatorLiveData<Event<Pair<Result<Unit>, Result<Unit>>>>()
    private val resultCounter = MediatorLiveData<Int>().apply { value = 0 }

    /**
     * According to documentation of [LiveData.postValue] :
     *      If you called this method multiple times before a main thread executed a posted task,
     *      only the last value would be dispatched.
     *
     * Our data sources updates the data on [LiveData] using this method as
     * we'r working on a background thread.
     *
     * To eliminate this situation we call our sources one by one.
     * TODO("Temp solution") : Multiple source updates LiveData
     */
    private var userInfo: UserInfoDetailed? = null

    init {
        val userInfoBasicResult = updateUserInfoBasicDataSource.observeUpdateResult()
        resultCounter.addSource(userInfoBasicResult) {
            resultCounter.value = resultCounter.value!! + 1
            userInfo?.let {
                // Second update the user info at Firestore
                updateFirestoreUserInfoDataSource.updateUserInfo(it)
            }
        }

        val userInfoOnFirestoreResult = updateFirestoreUserInfoDataSource.observeUpdateResult()
        resultCounter.addSource(userInfoOnFirestoreResult) {
            resultCounter.value = resultCounter.value!! + 1
        }

        result.addSource(resultCounter) {
            // Wait until both result get completed.
            if (it == 2) {
                result.postValue(
                    Event(Pair(userInfoBasicResult.value!!, userInfoOnFirestoreResult.value!!))
                )
                reset()
            }
        }
    }

    override fun updateUserInfo(userInfo: UserInfoDetailed) {
        this.userInfo = userInfo
        // First update the user info at Firebase
        updateUserInfoBasicDataSource.updateUserInfo(userInfo)
    }

    override fun observeUpdateResult(): LiveData<Event<Pair<Result<Unit>, Result<Unit>>>> {
        return result
    }

    private fun reset() {
        resultCounter.value = 0
        userInfo = null
    }
}