package com.pr656d.shared.domain.breeding.detail

import androidx.lifecycle.LiveData
import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import javax.inject.Inject

class GetBreedingByIdUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
) {
    operator fun invoke(breedingId: String): LiveData<Breeding?> {
        return breedingRepository.getBreeding(breedingId)
    }
}