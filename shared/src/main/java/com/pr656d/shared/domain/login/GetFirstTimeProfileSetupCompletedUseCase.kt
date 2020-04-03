package com.pr656d.shared.domain.login

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class GetFirstTimeProfileSetupCompletedUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, Boolean>() {
    override fun execute(parameters: Unit): Boolean {
        return preferenceStorage.firstTimeProfileSetupCompleted
    }
}