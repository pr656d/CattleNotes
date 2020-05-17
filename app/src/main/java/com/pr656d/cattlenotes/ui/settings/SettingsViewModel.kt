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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.cattlenotes.utils.getStringId
import com.pr656d.model.Theme
import com.pr656d.shared.domain.milk.sms.ObserveMilkSmsSourceUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.domain.settings.*
import org.threeten.bp.LocalTime
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    getAvailableThemesUseCase: GetAvailableThemesUseCase,
    getAutomaticMilkingCollectionUseCase: GetAutomaticMilkingCollectionUseCase,
    private val observePreferredTimeOfBreedingReminderUseCase: ObservePreferredTimeOfBreedingReminderUseCase,
    private val observeMilkSmsSourceUseCase: ObserveMilkSmsSourceUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setPreferredTimeOfBreedingReminderUseCase: SetPreferredTimeOfBreedingReminderUseCase,
    private val setAutomaticMilkingCollectionUseCase: SetAutomaticMilkingCollectionUseCase
) : ViewModel() {

    // Theme setting
    private val themeResult = MutableLiveData<Result<Theme>>()
    val theme: LiveData<Theme>

    // Theme setting
    private val availableThemesResult = MutableLiveData<Result<List<Theme>>>()
    val availableThemes: LiveData<List<Theme>>

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

    val preferredTimeOfBreedingReminder: LiveData<LocalTime>
        get() = observePreferredTimeOfBreedingReminderUseCase()

    val milkSmsSender: LiveData<Int>
        get() = observeMilkSmsSourceUseCase().map { it.getStringId() }

    private val _navigateToSmsSourceSelector = MutableLiveData<Event<Unit>>()
    val navigateToSmsSourceSelector: LiveData<Event<Unit>>
        get() = _navigateToSmsSourceSelector

    private val automaticMilkingCollectionResult = MutableLiveData<Result<Boolean>>()

    val automaticMilkingCollection: LiveData<Boolean>

    init {
        getThemeUseCase(Unit, themeResult)
        theme = themeResult.map {
            (it as? Success<Theme>)?.data ?: Theme.SYSTEM
        }

        getAvailableThemesUseCase(Unit, availableThemesResult)
        availableThemes = availableThemesResult.map {
            (it as? Success<List<Theme>>)?.data ?: emptyList()
        }

        getAutomaticMilkingCollectionUseCase(Unit, automaticMilkingCollectionResult)
        automaticMilkingCollection = automaticMilkingCollectionResult.map {
            (it as? Success)?.data ?: true
        }
    }

    fun setTheme(theme: Theme) {
        setThemeUseCase(theme)
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
        setPreferredTimeOfBreedingReminderUseCase(time)
    }

    fun toggleAutomaticMilkCollection(checked: Boolean) {
        setAutomaticMilkingCollectionUseCase(checked, automaticMilkingCollectionResult)
    }

    fun creditsClicked() {
        _navigateToCredits.value = Event(Unit)
    }

    fun openSourceLicensesClicked() {
        _navigateToOpenSourceLicenses.value = Event(Unit)
    }
}