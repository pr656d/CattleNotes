package com.pr656d.cattlenotes.shared.domain.launch

import com.pr656d.cattlenotes.data.local.prefs.PreferenceStorage
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class GetLoginCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, Boolean>() {
    override fun execute(parameters: Unit): Boolean {
        return preferenceStorage.loginCompleted.let { it }
    }
}