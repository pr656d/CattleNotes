package com.pr656d.cattlenotes.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.model.Theme
import com.pr656d.shared.data.prefs.datasource.SharedPreferenceStorage.Companion.DEFAULT_REMINDER_TIME
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.domain.settings.*
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalTime
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    getAvailableThemesUseCase: GetAvailableThemesUseCase,
    observePreferredTimeOfBreedingReminderUseCase: ObservePreferredTimeOfBreedingReminderUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setPreferredTimeOfBreedingReminderUseCase: SetPreferredTimeOfBreedingReminderUseCase
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

    private val _launchCredits = MutableLiveData<Event<Unit>>()
    val launchCredits: LiveData<Event<Unit>>
        get() = _launchCredits

    private val _launchOpenSourceLicense = MutableLiveData<Event<Unit>>()
    val launchOpenSourceLicense: LiveData<Event<Unit>>
        get() = _launchOpenSourceLicense

    val preferredTimeOfBreedingReminder: LiveData<LocalTime>

    init {
        getThemeUseCase(Unit, themeResult)
        theme = themeResult.map {
            (it as? Success<Theme>)?.data ?: Theme.SYSTEM
        }

        getAvailableThemesUseCase(Unit, availableThemesResult)
        availableThemes = availableThemesResult.map {
            (it as? Success<List<Theme>>)?.data ?: emptyList()
        }

        observePreferredTimeOfBreedingReminderUseCase.execute(Unit)
        preferredTimeOfBreedingReminder = observePreferredTimeOfBreedingReminderUseCase.observe().map {
            (it as? Success<LocalTime>)?.data ?: TimeUtils.toLocalTime(DEFAULT_REMINDER_TIME)
        }
    }

    fun setTheme(theme: Theme) {
        setThemeUseCase(theme)
    }

    fun onThemeSettingClicked() {
        _navigateToThemeSelector.value = Event(Unit)
    }

    fun onBreedingReminderClicked() {
        _navigateToBreedingReminderTimeSelector.postValue(Event(Unit))
    }

    fun setBreedingReminderTime(time: LocalTime) {
        setPreferredTimeOfBreedingReminderUseCase(time)
    }

    fun openCredits() {
        _launchCredits.postValue(Event(Unit))
    }

    fun openOpenSourceLicense() {
        _launchOpenSourceLicense.postValue(Event(Unit))
    }
}