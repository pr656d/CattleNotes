package com.pr656d.cattlenotes.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.model.Theme
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.domain.settings.GetAvailableThemesUseCase
import com.pr656d.shared.domain.settings.GetThemeUseCase
import com.pr656d.shared.domain.settings.SetThemeUseCase
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    val setThemeUseCase: SetThemeUseCase,
    getAvailableThemesUseCase: GetAvailableThemesUseCase
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

    init {
        getThemeUseCase(Unit, themeResult)
        theme = themeResult.map {
            (it as? Success<Theme>)?.data ?: Theme.SYSTEM
        }

        getAvailableThemesUseCase(Unit, availableThemesResult)
        availableThemes = availableThemesResult.map {
            (it as? Success<List<Theme>>)?.data ?: emptyList()
        }
    }

    fun setTheme(theme: Theme) {
        setThemeUseCase(theme)
    }

    fun onThemeSettingClicked() {
        _navigateToThemeSelector.value = Event(Unit)
    }
}