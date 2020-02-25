package com.pr656d.cattlenotes.shared.domain.settings

import com.pr656d.cattlenotes.data.local.prefs.PreferenceStorage
import com.pr656d.cattlenotes.data.model.Theme
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Theme, Unit>() {
    override fun execute(parameters: Theme) {
        preferenceStorage.selectedTheme = parameters.storageKey
    }
}