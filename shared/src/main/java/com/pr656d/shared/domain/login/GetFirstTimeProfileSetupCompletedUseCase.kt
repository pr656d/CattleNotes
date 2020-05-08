package com.pr656d.shared.domain.login

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class GetFirstTimeProfileSetupCompletedUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Unit, Boolean>() {
    override fun execute(parameters: Unit): Boolean {
        return preferenceStorageRepository.getFirstTimeProfileSetupCompleted()
    }
}