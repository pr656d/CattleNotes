package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class SetAutomaticMilkingCollectionUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Boolean, Boolean>() {
    override fun execute(parameters: Boolean): Boolean {
        preferenceStorageRepository.setAutomaticMilkingCollection(parameters)
        return preferenceStorageRepository.getAutomaticMilkingCollection()
    }
}