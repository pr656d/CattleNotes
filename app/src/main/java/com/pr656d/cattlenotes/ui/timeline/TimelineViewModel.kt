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
package com.pr656d.cattlenotes.ui.timeline

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.timeline.TimelineActionListener.ItemTimelineData
import com.pr656d.model.BreedingWithCattle
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    loadTimelineUseCase: LoadTimelineUseCase,
    private val updateBreedingUseCase: UpdateBreedingUseCase
) : ViewModel(), TimelineActionListener {

    @ExperimentalCoroutinesApi
    val timelineList: LiveData<List<BreedingWithCattle>> = loadTimelineUseCase(Unit)
        .map {
            _loading.postValue(false)
            it.successOr(emptyList())
        }
        .asLiveData()

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean>
        get() = _loading

    @ExperimentalCoroutinesApi
    val isEmpty: LiveData<Boolean>
        get() = timelineList.map { it.isNullOrEmpty() }

    private val _showMessage = MutableLiveData<Event<@StringRes Int>>()
    val showMessage = _showMessage

    private val _launchAddNewCattleScreen = MutableLiveData<Event<Cattle>>()
    val launchAddNewCattleScreen: LiveData<Event<Cattle>>
        get() = _launchAddNewCattleScreen

    override fun saveBreeding(itemTimelineData: ItemTimelineData, addNewCattle: Boolean) {
        viewModelScope.launch {
            _loading.postValue(true)

            val newBreeding = itemTimelineData.newBreedingWithCattle.breeding

            val updateResult = updateBreedingUseCase(newBreeding)

            // Launch add new cattle if update succeed and asked to add new cattle.
            if (updateResult is Result.Success && addNewCattle) {
                val cattle = itemTimelineData.newBreedingWithCattle.cattle
                _launchAddNewCattleScreen.postValue(Event(cattle))
            } else if (updateResult is Result.Error) {
                _showMessage.postValue(Event(R.string.error_unknown))
            }

            _loading.postValue(false)
        }
    }
}
