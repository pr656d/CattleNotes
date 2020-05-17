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

package com.pr656d.cattlenotes.ui.milking.list

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.model.Milk
import com.pr656d.shared.domain.milk.AddAllMilkUseCase
import com.pr656d.shared.domain.milk.LoadAllNewMilkFromSmsUseCase
import com.pr656d.shared.domain.milk.LoadMilkListUseCase
import com.pr656d.shared.domain.milk.sms.GetAvailableMilkSmsSourcesUseCase
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.milk.sms.SetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class MilkingViewModel @Inject constructor(
    loadMilkListUseCase: LoadMilkListUseCase,
    getAvailableMilkSmsSourcesUseCase: GetAvailableMilkSmsSourcesUseCase,
    getPreferredMilkSmsSourceUseCase: GetPreferredMilkSmsSourceUseCase,
    private val setPreferredMilkSmsSourceUseCase: SetPreferredMilkSmsSourceUseCase,
    private val loadAllNewMilkFromSmsUseCase: LoadAllNewMilkFromSmsUseCase,
    private val addAllMilkUseCase: AddAllMilkUseCase
) : ViewModel(), MilkingActionListener {

    private val addMilkUseCaseResult = MutableLiveData<Result<Unit>>()
    private val loadAllNewMilkFromSmsResult = MutableLiveData<Result<List<Milk>>>()
    private val availableMilkSmsSourcesResult = MutableLiveData<Result<List<Milk.Source.Sms>>>()

    val milkList: LiveData<List<Milk>> = loadMilkListUseCase()

    val isEmpty: LiveData<Boolean>

    private val _loading = MediatorLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _permissionsGranted = MutableLiveData<Boolean>(true)
    val permissionsGranted: LiveData<Boolean>
        get() = _permissionsGranted

    private val _requestPermissions = MutableLiveData<Event<Unit>>()
    val requestPermissions: LiveData<Event<Unit>>
        get() = _requestPermissions

    private val _showPermissionExplanation = MutableLiveData<Event<Unit>>()
    val showPermissionExplanation: LiveData<Event<Unit>>
        get() = _showPermissionExplanation

    val availableMilkSmsSources: LiveData<List<Milk.Source.Sms>>
        get() = availableMilkSmsSourcesResult.map {
            (it as? Result.Success)?.data ?: emptyList()
        }

    private val _smsSource = MediatorLiveData<Milk.Source.Sms>()
    val smsSource: LiveData<Milk.Source.Sms>
        get() = _smsSource

    val newMilkListFromSms: LiveData<List<Milk>>
        get() = loadAllNewMilkFromSmsResult.map {
            (it as? Result.Success)?.data ?: emptyList()
        }

    private val _navigateToSmsSourceSelector = MutableLiveData<Event<Unit>>()
    val navigateToSmsSourceSelector: LiveData<Event<Unit>>
        get() = _navigateToSmsSourceSelector

    // Flag to start sync with sms messages right after sms source is set.
    private var syncWithSmsMessagesAfterSmsSourceIsSet: Event<Unit>? = null

    private val _saveNewMilkConfirmationDialog = MediatorLiveData<Event<List<Milk>>>()
    val saveNewMilkConfirmationDialog: LiveData<Event<List<Milk>>>
        get() = _saveNewMilkConfirmationDialog

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val _navigateToAddMilk = MutableLiveData<Event<Unit>>()
    val navigateToAddMilk: LiveData<Event<Unit>>
        get() = _navigateToAddMilk

    init {
        _smsSource.addSource(getPreferredMilkSmsSourceUseCase(Unit)) { result ->
            (result as? Result.Success)?.data.let {
                _smsSource.value = it
            }
        }

        getAvailableMilkSmsSourcesUseCase(Unit, availableMilkSmsSourcesResult)

        _saveNewMilkConfirmationDialog.addSource(newMilkListFromSms) {
            _saveNewMilkConfirmationDialog.value = Event(it)
        }

        _loading.addSource(milkList) {
            _loading.value = false
        }

        _loading.addSource(addMilkUseCaseResult) {
            _loading.value = false
        }

        isEmpty = milkList.map { it.isNullOrEmpty() }

        _showMessage.addSource(addMilkUseCaseResult) {
            (it as? Result.Error)?.exception?.let {
                _showMessage.value = Event(R.string.error_add_milk)
            }
        }
    }

    fun setPermissionsGranted(isGranted: Boolean) {
        _permissionsGranted.value = isGranted
        checkAndSyncWithSmsMessages()
    }

    /**
     * Check if we need to start syncing with SMS messages.
     * Start syncing only when permission is granted and previously requested for permission.
     */
    private fun checkAndSyncWithSmsMessages() {
        if (isPermissionGranted() && requestedForPermission())
            syncWithSmsMessages()
    }

    fun changeSmsSource() {
        _navigateToSmsSourceSelector.value = Event(Unit)
    }

    fun requestPermission() {
        _requestPermissions.value = Event(Unit)
    }

    fun showPermissionExplanation() {
        _showPermissionExplanation.value = Event(Unit)
    }

    fun addMilk() {
        _navigateToAddMilk.value = Event(Unit)
    }

    fun syncWithSmsMessages() {
        if (!isPermissionGranted()) {
            requestPermission()
            return
        }

        if (_smsSource.value == null) {
            /** Set event so that when sms source is set we can start syncing right after that. */
            syncWithSmsMessagesAfterSmsSourceIsSet = Event(Unit)
            // Sms sender not available.
            _navigateToSmsSourceSelector.value = Event(Unit)
            return
        }

        // Sms sender available.
        loadAllNewMilkFromSmsUseCase(_smsSource.value!!, loadAllNewMilkFromSmsResult)
    }

    fun setSmsSource(smsSource: Milk.Source.Sms) {
        setPreferredMilkSmsSourceUseCase(smsSource)
        _smsSource.value = smsSource
        checkAndSyncWithSmsMessagesAfterSmsSourceIsSet()
    }

    private fun checkAndSyncWithSmsMessagesAfterSmsSourceIsSet() {
        // Check if we have to start syncing for sms messages.
        syncWithSmsMessagesAfterSmsSourceIsSet?.getContentIfNotHandled()?.let {
            syncWithSmsMessages()
            syncWithSmsMessagesAfterSmsSourceIsSet = null  // Reset to null
        }
    }

    private fun requestedForPermission() = _requestPermissions.value?.peekContent() != null

    private fun isPermissionGranted() = _permissionsGranted.value == true

    fun saveMilk(milkList: List<Milk>) {
        _loading.value = true
        addAllMilkUseCase(milkList, addMilkUseCaseResult)
    }

    override fun delete(milk: Milk) {
        // TODO("Not yet implemented")
    }
}