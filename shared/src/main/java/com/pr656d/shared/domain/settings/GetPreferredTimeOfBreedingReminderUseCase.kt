package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import org.threeten.bp.LocalTime
import javax.inject.Inject

class GetPreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Unit, LocalTime>() {
    override fun execute(parameters: Unit): LocalTime {
        return preferenceStorageRepository.getPreferredTimeOfBreedingReminder()
    }
}