package com.pr656d.cattlenotes.ui.settings

import com.pr656d.model.Theme
import com.pr656d.model.themeFromStorageKey
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

open class ObserveThemeModeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : MediatorUseCase<Unit, Theme>() {
    override fun execute(parameters: Unit) {
        result.addSource(preferenceStorage.observableSelectedTheme) {
            result.postValue(Result.Success(themeFromStorageKey(it)))
        }
    }
}