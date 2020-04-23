package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import org.threeten.bp.LocalTime
import javax.inject.Inject

class SetPreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater,
    private val authIdDataSource: AuthIdDataSource
) : UseCase<LocalTime, Unit>() {
    override fun execute(parameters: LocalTime) {
        val userId = authIdDataSource.getUserId()
            ?: throw IllegalStateException("User id not found")

        preferenceStorage.preferredTimeOfBreedingReminder = parameters.toNanoOfDay()
        breedingNotificationAlarmUpdater.updateAll(userId)
    }
}