package com.pr656d.shared.domain.login

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class SetFirstTimeProfileSetupCompletedUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Boolean, Unit>() {
    override fun execute(parameters: Boolean) {
        preferenceStorageRepository.setFirstTimeProfileSetupCompleted(parameters)
    }
}