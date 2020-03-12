package com.pr656d.shared.domain.settings

import com.pr656d.model.Theme
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Theme, Unit>() {
    override fun execute(parameters: Theme) {
        preferenceStorage.selectedTheme = parameters.storageKey
    }
}