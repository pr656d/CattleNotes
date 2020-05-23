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

package com.pr656d.cattlenotes.test.fakes.data.cattle

import com.pr656d.cattlenotes.test.fakes.data.FakeAppDatabase
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Fake of [CattleRepository].
 *
 * Whoever needs this will override methods with desired output needed.
 * This will allow us not to implement unnecessary methods every time we create it.
 *
 * Example:
 *      val fakeCattleRepository = object : FakeCattleRepository {
 *             // Override whatever method you want to use.
 *      }
 */
open class FakeCattleRepository(
    private val fakeAppDatabase: FakeAppDatabase = FakeAppDatabase()
) : CattleRepository {

    override suspend fun addCattle(cattle: Cattle): Long {
        return fakeAppDatabase.cattleDao().insert(cattle)
    }

    override suspend fun addAllCattle(cattleList: List<Cattle>): List<Long> {
        return fakeAppDatabase.cattleDao().insertAll(cattleList)
    }

    override fun getAllCattle(): Flow<List<Cattle>> {
        return fakeAppDatabase.cattleDao().getAll()
    }

    override suspend fun load() { }

    override fun getCattleById(id: String): Flow<Cattle?> {
        return fakeAppDatabase.cattleDao().getById(id)
    }

    override fun getCattleByTagNumber(tagNumber: Long): Flow<Cattle?> {
        return fakeAppDatabase.cattleDao().getByTagNumber(tagNumber)
    }

    override suspend fun deleteCattle(cattle: Cattle) {
        return fakeAppDatabase.cattleDao().delete(cattle)
    }

    override suspend fun updateCattle(cattle: Cattle) {
        return fakeAppDatabase.cattleDao().update(cattle)
    }

    override suspend fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
        return fakeAppDatabase.cattleDao().getRowCountWithMatchingTagNumber(tagNumber) > 0
    }
}