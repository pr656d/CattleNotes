package com.pr656d.shared.domain.launch

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class SetLoginCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Boolean, Unit>() {
    override fun execute(parameters: Boolean) {
        preferenceStorage.loginCompleted = parameters
    }
}