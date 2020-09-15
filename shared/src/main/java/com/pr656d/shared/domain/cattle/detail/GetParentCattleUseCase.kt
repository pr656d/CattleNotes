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
package com.pr656d.shared.domain.cattle.detail

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.domain.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Return parent cattle of the cattle.
 */
open class GetParentCattleUseCase @Inject constructor(
    private val cattleRepository: CattleRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<Cattle, Cattle?>(ioDispatcher) {

    override suspend fun execute(parameters: Cattle): Cattle? {
        val parentId = parameters.parent ?: return null
        return cattleRepository.getCattleById(parentId).first()
    }
}
