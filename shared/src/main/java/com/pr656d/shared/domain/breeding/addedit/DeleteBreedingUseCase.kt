package com.pr656d.shared.domain.breeding.addedit

import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import javax.inject.Inject

open class DeleteBreedingUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository,
    private val breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater
) : UseCase<Breeding, Unit>() {
    override fun execute(parameters: Breeding) {
        breedingRepository.deleteBreeding(parameters)
        breedingNotificationAlarmUpdater.cancelByBreedingId(parameters.id)
    }
}