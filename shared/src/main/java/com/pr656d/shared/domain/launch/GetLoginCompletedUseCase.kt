package com.pr656d.shared.domain.launch

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class GetLoginCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, Boolean>() {
    override fun execute(parameters: Unit): Boolean {
        return preferenceStorage.loginCompleted
    }
}