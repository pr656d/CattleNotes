package com.pr656d.cattlenotes.shared.domain.settings

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES.Q
import com.pr656d.cattlenotes.data.local.prefs.PreferenceStorage
import com.pr656d.cattlenotes.data.model.Theme
import com.pr656d.cattlenotes.data.model.themeFromStorageKey
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, Theme>() {
    override fun execute(parameters: Unit): Theme {
        preferenceStorage.selectedTheme?.let { key ->
            return themeFromStorageKey(key)
        }
        // If we get here, we don't currently have a theme set, so we need to provide a default
        return when {
            VERSION.SDK_INT >= Q -> Theme.SYSTEM
            else -> Theme.BATTERY_SAVER
        }
    }
}