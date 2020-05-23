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

package com.pr656d.cattlenotes.ui.cattle.list

import androidx.lifecycle.*
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.list.LoadCattleListUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.successOr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    loadCattleListUseCase: LoadCattleListUseCase
) : ViewModel(), CattleActionListener {

    @ExperimentalCoroutinesApi
    val cattleList: LiveData<List<Cattle>> = loadCattleListUseCase(Unit)
        .map {
            _loading.postValue(false)
            it.successOr(emptyList())
        }
        .asLiveData()

    private val _launchAddCattleScreen = MutableLiveData<Event<Unit>>()
    val launchAddCattleScreen: LiveData<Event<Unit>>
        get() = _launchAddCattleScreen

    private val _launchCattleDetail = MutableLiveData<Event<Cattle>>()
    val launchCattleDetail: LiveData<Event<Cattle>>
        get() = _launchCattleDetail

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean>
        get() = _loading

    @ExperimentalCoroutinesApi
    val isEmpty: LiveData<Boolean>
        get() = cattleList.map { it.isNullOrEmpty() }

    fun addCattle() {
        _launchAddCattleScreen.value = Event(Unit)
    }

    override fun openCattle(cattle: Cattle) {
        _launchCattleDetail.value = Event(cattle)
    }
}