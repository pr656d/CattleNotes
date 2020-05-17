/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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