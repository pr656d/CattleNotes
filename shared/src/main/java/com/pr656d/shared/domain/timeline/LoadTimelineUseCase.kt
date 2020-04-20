package com.pr656d.shared.domain.timeline

import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

open class LoadTimelineUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository
) : MediatorUseCase<Unit, List<BreedingWithCattle>>() {
    override fun execute(parameters: Unit) {
        result.addSource(breedingRepository.getAllBreedingWithCattle()) { list ->
            DefaultScheduler.execute {
                val filteredList = list
                    .filter {
                        // We need only incomplete breeding
                        !it.breeding.breedingCompleted
                    }
                    .sortedBy { it.breeding.nextBreedingEvent?.expectedOn }

                result.postValue(Result.Success(filteredList))
            }
        }
    }
}