package com.pr656d.shared.domain.breeding.list

import androidx.lifecycle.LiveData
import com.pr656d.model.BreedingCycle
import com.pr656d.shared.data.breeding.BreedingRepository
import javax.inject.Inject

open class LoadObservableBreedingListUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
) {
    operator fun invoke(): LiveData<List<BreedingCycle>> {
        return breedingRepository.getObservableAllBreeding()
    }
}