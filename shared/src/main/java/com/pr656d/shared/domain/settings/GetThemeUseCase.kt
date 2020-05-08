package com.pr656d.shared.domain.settings

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES.Q
import com.pr656d.model.Theme
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Unit, Theme>() {
    override fun execute(parameters: Unit): Theme {
        return preferenceStorageRepository.getSelectedTheme()
            ?: when {
                // If we get here, we don't currently have a theme set, so we need to provide a default
                VERSION.SDK_INT >= Q -> Theme.SYSTEM
                else -> Theme.BATTERY_SAVER
            }
    }
}