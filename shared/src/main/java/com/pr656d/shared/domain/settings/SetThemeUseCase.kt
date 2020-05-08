package com.pr656d.shared.domain.settings

import com.pr656d.model.Theme
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Theme, Unit>() {
    override fun execute(parameters: Theme) {
        preferenceStorageRepository.setSelectedTheme(parameters)
    }
}