package com.pr656d.shared.domain.breeding.history

import androidx.lifecycle.LiveData
import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import javax.inject.Inject

open class LoadBreedingByCattleIdUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
) {
    operator fun invoke(cattleId: String): LiveData<List<Breeding>> {
        return breedingRepository.getAllBreedingByCattleId(cattleId)
    }
}