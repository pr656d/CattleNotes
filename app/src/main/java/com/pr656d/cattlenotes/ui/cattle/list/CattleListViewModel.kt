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
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    loadCattleListUseCase: LoadCattleListUseCase
) : ViewModel(), CattleActionListener {

    val cattleList: LiveData<List<Cattle>> = loadCattleListUseCase()

    private val _launchAddCattleScreen = MutableLiveData<Event<Unit>>()
    val launchAddCattleScreen: LiveData<Event<Unit>> = _launchAddCattleScreen

    private val _launchCattleDetail = MutableLiveData<Event<Cattle>>()
    val launchCattleDetail: LiveData<Event<Cattle>> = _launchCattleDetail

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>

    init {
        isEmpty = cattleList.map { it.isNullOrEmpty() }

        _loading.addSource(cattleList) {
            _loading.value = false
        }
    }

    fun addCattle() {
        _launchAddCattleScreen.value = Event(Unit)
    }

    override fun openCattle(cattle: Cattle) {
        _launchCattleDetail.value = Event(cattle)
    }
}