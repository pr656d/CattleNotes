/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.domain.breeding.detail

import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.domain.FlowUseCase
import com.pr656d.shared.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBreedingByIdUseCase @Inject constructor(
    private val breedingRepository: BreedingRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<String, Breeding?>(ioDispatcher) {

    override fun execute(parameters: String): Flow<Result<Breeding?>> =
        breedingRepository.getBreedingById(parameters).map { Result.Success(it) }
}
