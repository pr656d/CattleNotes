package com.pr656d.shared.domain.cattle.addedit

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.domain.internal.DefaultScheduler
import javax.inject.Inject

open class DeleteCattleUseCase @Inject constructor(
    private val cattleRepository: CattleRepository,
    private val breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater
) : UseCase<Cattle, Unit>() {
    override fun execute(parameters: Cattle) {
        // Cancel alarms for this cattle's breedings.
        breedingNotificationAlarmUpdater.cancelByCattleId(parameters.id) {
            // Wait until alarm is cancelled.
            DefaultScheduler.execute {
                cattleRepository.deleteCattle(parameters)
            }
        }
    }
}