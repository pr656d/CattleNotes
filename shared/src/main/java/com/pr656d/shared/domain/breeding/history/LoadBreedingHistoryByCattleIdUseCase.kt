package com.pr656d.shared.domain.breeding.history

import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class LoadBreedingHistoryByCattleIdUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
): UseCase<Long, List<Breeding>>() {
    override fun execute(parameters: Long): List<Breeding> {
        return breedingRepository.getAllBreedingByCattleId(parameters)
    }
}