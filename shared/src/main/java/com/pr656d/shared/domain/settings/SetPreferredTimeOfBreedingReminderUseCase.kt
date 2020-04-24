package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import org.threeten.bp.LocalTime
import javax.inject.Inject

class SetPreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<LocalTime, Unit>() {
    override fun execute(parameters: LocalTime) {
        preferenceStorage.preferredTimeOfBreedingReminder = parameters.toNanoOfDay()
    }
}