package com.pr656d.shared.domain.breeding.addedit

import com.pr656d.model.BreedingCycle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class UpdateBreedingUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
) : UseCase<BreedingCycle, Unit>() {
    override fun execute(parameters: BreedingCycle) {
        breedingRepository.updateBreeding(parameters)
    }
}