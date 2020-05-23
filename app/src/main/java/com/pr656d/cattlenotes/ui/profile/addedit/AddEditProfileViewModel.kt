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

package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.succeeded
import com.pr656d.shared.domain.result.successOr
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditProfileViewModel @Inject constructor(
    profileDelegate: ProfileDelegate
) : ViewModel(),
    ProfileDelegate by profileDelegate {

    private val _navigateUp = MediatorLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>> = _navigateUp

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        _loading.addSource(currentUserInfo) {
            _loading.postValue(false)
        }

        _loading.addSource(savingProfile) {
            _loading.postValue(it)
        }
    }

    fun save() {
        viewModelScope.launch {
            val result = saveProfile()

            result.successOr(null)?.getContentIfNotHandled()?.let {
                if (it.first.succeeded && it.second.succeeded)
                    navigateUp()
            }
        }
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }
}