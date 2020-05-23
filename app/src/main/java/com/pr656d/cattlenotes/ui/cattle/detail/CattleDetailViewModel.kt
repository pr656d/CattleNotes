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

package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.succeeded
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.utils.nameOrTagNumber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CattleDetailViewModel @Inject constructor(
    private val getCattleByIdUseCase: GetCattleByIdUseCase,
    private val deleteCattleUseCase: DeleteCattleUseCase
) : ViewModel() {
    private val cattleId = MutableLiveData<String>()

    val cattle: LiveData<Cattle?> = cattleId.switchMap { id ->
        _loading.postValue(true)

        getCattleByIdUseCase(id)
            .map {
                _loading.postValue(false)
                it.successOr(null)
            }
            .asLiveData()
    }

    val isCattleTypeBull: LiveData<Boolean> = cattle.map { it?.type is AnimalType.Bull }

    /**  Cattle's parent detail  */
    val parentCattle: LiveData<Cattle?> = cattle.switchMap {
        val parentId = it?.parent
            ?: return@switchMap MutableLiveData<Cattle?>(null)

        _loadingParent.postValue(true)

        getCattleByIdUseCase(parentId)
            .map { result ->
                _loading.postValue(false)
                result.successOr(null)
            }
            .asLiveData()
    }

    val parent:LiveData<String?> = parentCattle.map { it?.nameOrTagNumber() }

    val isParentCattleTypeBull: LiveData<Boolean> = parentCattle.map { it?.type is AnimalType.Bull }

    /**  Parent cattle's parent detail.  */
    val parentParentCattle: LiveData<Cattle?> = parentCattle.switchMap {
        val parentId = it?.parent
            ?: return@switchMap MutableLiveData<Cattle?>(null)

        _loadingParent.postValue(true)

        getCattleByIdUseCase(parentId)
            .map { result ->
                _loadingParent.postValue(false)
                result.successOr(null)
            }
            .asLiveData()
    }

    val parentParent:LiveData<String?> = parentParentCattle.map { it?.nameOrTagNumber() }

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val _launchAllBreeding = MutableLiveData<Event<Cattle>>()
    val launchAllBreeding: LiveData<Event<Cattle>>
        get() = _launchAllBreeding

    private val _launchAddBreeding = MutableLiveData<Event<Cattle>>()
    val launchAddBreeding: LiveData<Event<Cattle>>
        get() = _launchAddBreeding

    private val _launchEditCattle = MutableLiveData<Event<Cattle>>()
    val launchEditCattle: LiveData<Event<Cattle>>
        get() = _launchEditCattle

    private val _launchDeleteConfirmation = MutableLiveData<Event<Unit>>()
    val launchDeleteConfirmation: LiveData<Event<Unit>>
        get() = _launchDeleteConfirmation

    private val _navigateUp = MediatorLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _loadingParent = MutableLiveData(true)
    val loadingParent: LiveData<Boolean>
        get() = _loadingParent

    private val _showParentDetail = MutableLiveData<Event<Unit>>()
    val showParentDetail: LiveData<Event<Unit>>
        get() = _showParentDetail

    init {
        _showMessage.addSource(cattle) {
            if (it == null) showMessage(R.string.error_cattle_not_found)
        }
    }

    fun fetchCattle(id: String) = cattleId.postValue(id)

    fun showParentDetail() = _showParentDetail.postValue(Event(Unit))

    fun showAllBreeding() {
        cattle.value?.let {
            // Bull doesn't have breeding
            if (isCattleTypeBull.value == false)
                _launchAllBreeding.postValue(Event(it))
        }
    }

    fun addNewBreeding() {
        cattle.value?.let {
            // Bull doesn't have breeding
            if (isCattleTypeBull.value == false)
                _launchAddBreeding.postValue(Event(it))
        }
    }

    fun editCattle() {
        cattle.value?.let {
            _launchEditCattle.postValue(Event(it))
        }
    }

    fun deleteCattle(deleteConfirmation: Boolean = false) {
        if (deleteConfirmation) {
            viewModelScope.launch {
                _loading.postValue(true)

                val result = deleteCattleUseCase(cattle.value!!)

                if (result.succeeded)
                    navigateUp()
                else
                    showMessage()

                _loading.postValue(false)
            }
        } else {
            deleteCattleConfirmation()
        }
    }

    private fun deleteCattleConfirmation() {
        _launchDeleteConfirmation.postValue(Event(Unit))
    }

    private fun showMessage(@StringRes messageId: Int = R.string.error_unknown) {
        _showMessage.postValue(Event(messageId))
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }
}