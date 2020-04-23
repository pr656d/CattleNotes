package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalTime
import javax.inject.Inject

class GetPreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, LocalTime>() {
    override fun execute(parameters: Unit): LocalTime {
        return TimeUtils.toLocalTime(preferenceStorage.preferredTimeOfBreedingReminder)
    }
}