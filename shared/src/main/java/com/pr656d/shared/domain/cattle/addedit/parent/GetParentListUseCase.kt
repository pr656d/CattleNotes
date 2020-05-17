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

package com.pr656d.shared.domain.cattle.addedit.parent

import com.pr656d.model.Cattle
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.cattle.list.LoadCattleListUseCase
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class GetParentListUseCase @Inject constructor(
    private val loadCattleListUseCase: LoadCattleListUseCase
) : MediatorUseCase<Long, List<Cattle>>() {
    override fun execute(parameters: Long) {
        result.addSource(loadCattleListUseCase()) { list ->
            DefaultScheduler.execute {
                val filteredList = list.filter {
                    it.tagNumber != parameters
                }
                result.postValue(Result.Success(filteredList))
            }
        }
    }
}