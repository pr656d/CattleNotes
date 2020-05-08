package com.pr656d.shared.domain.login

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class GetLoginCompletedUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Unit, Boolean>() {
    override fun execute(parameters: Unit): Boolean {
        return preferenceStorageRepository.getLoginCompleted()
    }
}