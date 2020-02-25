package com.pr656d.cattlenotes.shared.domain.launch

import com.pr656d.cattlenotes.data.local.prefs.PreferenceStorage
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class SetLoginCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Boolean, Unit>() {
    override fun execute(parameters: Boolean) {
        preferenceStorage.loginCompleted = parameters
    }
}