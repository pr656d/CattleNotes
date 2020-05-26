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

package com.pr656d.cattlenotes.ui.settings

import androidx.lifecycle.*
import com.pr656d.cattlenotes.utils.getStringId
import com.pr656d.model.Theme
import com.pr656d.shared.domain.invoke
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.domain.result.updateOnSuccess
import com.pr656d.shared.domain.settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    getAvailableThemesUseCase: GetAvailableThemesUseCase,
    getAutomaticMilkingCollectionUseCase: GetAutomaticMilkingCollectionUseCase,
    private val setAutomaticMilkingCollectionUseCase: SetAutomaticMilkingCollectionUseCase,
    private val observePreferredTimeOfBreedingReminderUseCase: ObservePreferredTimeOfBreedingReminderUseCase,
    getPreferredMilkSmsSourceUseCase: GetPreferredMilkSmsSourceUseCase,
    private val setPreferredTimeOfBreedingReminderUseCase: SetPreferredTimeOfBreedingReminderUseCase
) : ViewModel() {

    // Theme setting
    val theme: LiveData<Theme> = liveData {
        getThemeUseCase().successOr(null)?.let { emit(it) }
    }

    // Theme setting
    val availableThemes: LiveData<List<Theme>> = liveData {
        getAvailableThemesUseCase().successOr(null)?.let {
            emit(it)
        }
    }

    private val _navigateToThemeSelector = MutableLiveData<Event<Unit>>()
    val navigateToThemeSelector: LiveData<Event<Unit>>
        get() = _navigateToThemeSelector

    private val _navigateToBreedingReminderTimeSelector = MutableLiveData<Event<Unit>>()
    val navigateToBreedingReminderTimeSelector: LiveData<Event<Unit>>
        get() = _navigateToBreedingReminderTimeSelector

    private val _navigateToCredits = MutableLiveData<Event<Unit>>()
    val navigateToCredits: LiveData<Event<Unit>>
        get() = _navigateToCredits

    private val _navigateToOpenSourceLicenses = MutableLiveData<Event<Unit>>()
    val navigateToOpenSourceLicenses: LiveData<Event<Unit>>
        get() = _navigateToOpenSourceLicenses

    @ExperimentalCoroutinesApi
    val preferredTimeOfBreedingReminder: LiveData<LocalTime> =
        observePreferredTimeOfBreedingReminderUseCase(Unit)
            .map { it.successOr(null) }
            .filterNotNull()
            .asLiveData()

    val milkSmsSender: LiveData<Int?> = liveData {
        getPreferredMilkSmsSourceUseCase().successOr(null)
            ?.getStringId()
            .let { emit(it) }
    }

    private val _navigateToSmsSourceSelector = MutableLiveData<Event<Unit>>()
    val navigateToSmsSourceSelector: LiveData<Event<Unit>>
        get() = _navigateToSmsSourceSelector

    val automaticMilkingCollection = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            getAutomaticMilkingCollectionUseCase().successOr(null).let {
                automaticMilkingCollection.postValue(it ?: true)
            }
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun onThemeSettingClicked() {
        _navigateToThemeSelector.value = Event(Unit)
    }

    fun onBreedingReminderClicked() {
        _navigateToBreedingReminderTimeSelector.value = Event(Unit)
    }

    fun onMilkSmsSenderClicked() {
        _navigateToSmsSourceSelector.value = Event(Unit)
    }

    fun setBreedingReminderTime(time: LocalTime) {
        viewModelScope.launch {
            setPreferredTimeOfBreedingReminderUseCase(time)
        }
    }

    fun toggleAutomaticMilkCollection(checked: Boolean) {
        viewModelScope.launch {
            val result = setAutomaticMilkingCollectionUseCase(checked)
            result.updateOnSuccess(automaticMilkingCollection)
        }
    }

    fun creditsClicked() {
        _navigateToCredits.value = Event(Unit)
    }

    fun openSourceLicensesClicked() {
        _navigateToOpenSourceLicenses.value = Event(Unit)
    }
}