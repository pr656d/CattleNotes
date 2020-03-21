package com.pr656d.shared.domain.breeding.history

import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class LoadBreedingHistoryUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
): UseCase<Unit, List<BreedingWithCattle>>() {
    override fun execute(parameters: Unit): List<BreedingWithCattle> {
        return breedingRepository.getAllBreedingWithCattle()
    }
}