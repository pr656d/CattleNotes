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
package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryActionListener
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.DeleteBreedingUseCase
import com.pr656d.shared.domain.breeding.history.LoadBreedingByCattleIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.utils.nameOrTagNumber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
class BreedingHistoryOfCattleViewModel @Inject constructor(
    private val loadBreedingByCattleIdUseCase: LoadBreedingByCattleIdUseCase,
    private val deleteBreedingUseCase: DeleteBreedingUseCase,
    getCattleByIdUseCase: GetCattleByIdUseCase
) : ViewModel(), BreedingHistoryActionListener {

    private val cattleId = MutableLiveData<String>()

    val cattle: LiveData<Cattle?> = cattleId.switchMap { id ->
        getCattleByIdUseCase(id)
            .map { it.successOr(null) }
            .asLiveData()
    }

    val nameOrTagNumber = cattle.map { it?.nameOrTagNumber() }

    val breedingList: LiveData<List<Breeding>> = cattleId.switchMap { id ->
        loadBreedingByCattleIdUseCase(id)
            .map {
                _loading.postValue(false)
                it.successOr(emptyList())
            }
            .asLiveData()
    }

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>
        get() = breedingList.map { it.isNullOrEmpty() }

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val _launchDeleteConfirmation = MutableLiveData<Event<Breeding>>()
    val launchDeleteConfirmation: LiveData<Event<Breeding>>
        get() = _launchDeleteConfirmation

    private val _launchEditBreeding = MutableLiveData<Event<Pair<Cattle, Breeding>>>()
    val launchEditBreeding: LiveData<Event<Pair<Cattle, Breeding>>>
        get() = _launchEditBreeding

    fun setCattle(id: String) = cattleId.postValue(id)

    override fun editBreeding(breeding: Breeding) {
        _launchEditBreeding.postValue(Event(cattle.value!! to breeding))
    }

    override fun deleteBreeding(breeding: Breeding, deleteConfirmation: Boolean) {
        if (deleteConfirmation)
            viewModelScope.launch {
                deleteBreedingUseCase(breeding).let { result ->
                    (result as? Result.Error)?.exception?.let {
                        _showMessage.postValue(Event(R.string.error_unknown))
                    }
                }
            }
        else
            deleteBreedingConfirmation(breeding)
    }

    private fun deleteBreedingConfirmation(breeding: Breeding) {
        _launchDeleteConfirmation.postValue(Event(breeding))
    }
}
