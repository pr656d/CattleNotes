package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import org.threeten.bp.LocalTime
import javax.inject.Inject

class SetPreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<LocalTime, Unit>() {
    override fun execute(parameters: LocalTime) {
        preferenceStorageRepository.setPreferredTimeOfBreedingReminder(parameters)
    }
}