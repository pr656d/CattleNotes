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

package com.pr656d.cattlenotes.ui.timeline

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.timeline.TimelineActionListener.ItemTimelineData
import com.pr656d.model.BreedingWithCattle
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    loadTimelineUseCase: LoadTimelineUseCase,
    private val updateBreedingUseCase: UpdateBreedingUseCase
) : ViewModel(), TimelineActionListener {

    private val breedingListResult = loadTimelineUseCase.observe()

    private val updateBreedingResult = MutableLiveData<Result<Unit>>()

    private val _timelineList = MediatorLiveData<List<BreedingWithCattle>>()
    val timelineList: LiveData<List<BreedingWithCattle>> = _timelineList

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage = _showMessage

    private var launchAddNewCattleWhenSaveCompleted = false

    /**
     * This is to hold [Cattle] for temporary purpose only.
     */
    private var itemCattle: Cattle? = null

    private val _launchAddNewCattleScreen = MediatorLiveData<Event<Cattle>>()
    val launchAddNewCattleScreen: LiveData<Event<Cattle>>
        get() = _launchAddNewCattleScreen

    init {
        loadTimelineUseCase.execute(Unit)

        _timelineList.addSource(breedingListResult) { result ->
            (result as? Result.Success)?.data?.let {
                _timelineList.postValue(it)
            }
        }

        isEmpty = timelineList.map { it.isNullOrEmpty() }

        _loading.addSource(timelineList) {
            _loading.postValue(false)
        }

        _showMessage.addSource(updateBreedingResult) {
            (it as? Result.Error)?.exception?.let { e ->
                _showMessage.postValue(Event(R.string.error_unknown))
            }
        }

        _launchAddNewCattleScreen.addSource(updateBreedingResult) {
            if (launchAddNewCattleWhenSaveCompleted)
                (it as? Result.Success)?.let {
                    _launchAddNewCattleScreen.postValue(Event(itemCattle!!))
                    itemCattle = null
                }
        }
    }

    override fun saveBreeding(itemTimelineData: ItemTimelineData, addNewCattle: Boolean) {
        val newBreeding = itemTimelineData.newBreedingWithCattle.breeding
        itemCattle = itemTimelineData.newBreedingWithCattle.cattle
        launchAddNewCattleWhenSaveCompleted = addNewCattle
        updateBreedingUseCase(newBreeding, updateBreedingResult)
    }
}
