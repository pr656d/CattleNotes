package com.pr656d.shared.domain.breeding.addedit

import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class AddBreedingUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
) : UseCase<Breeding, Unit>() {
    override fun execute(parameters: Breeding) {
        breedingRepository.addBreeding(parameters)
    }
}