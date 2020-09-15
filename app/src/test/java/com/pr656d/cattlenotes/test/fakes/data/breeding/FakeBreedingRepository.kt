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
package com.pr656d.cattlenotes.test.fakes.data.breeding

import com.pr656d.cattlenotes.test.fakes.data.FakeAppDatabase
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.BreedingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Fake of [BreedingRepository].
 *
 * Whoever needs this will override methods with desired output needed.
 * This will allow us not to implement unnecessary methods every time we create it.
 *
 * Example:
 *      val fakeBreedingRepository = object : FakeBreedingRepository {
 *             // Override whatever method you want to use.
 *      }
 */
open class FakeBreedingRepository(
    private val fakeAppDatabase: FakeAppDatabase = FakeAppDatabase()
) : BreedingRepository {
    override suspend fun addBreeding(breeding: Breeding): Long {
        return fakeAppDatabase.breedingDao().insert(breeding)
    }

    override suspend fun addAllBreeding(breedingList: List<Breeding>): List<Long> {
        return fakeAppDatabase.breedingDao().insertAll(breedingList)
    }

    override suspend fun load() {}

    override fun getAllBreeding(): Flow<List<Breeding>> {
        return fakeAppDatabase.breedingDao().getAll()
    }

    override fun getBreedingById(breedingId: String): Flow<Breeding?> {
        return fakeAppDatabase.breedingDao().getById(breedingId)
    }

    override fun getAllBreedingByCattleId(cattleId: String): Flow<List<Breeding>> {
        return fakeAppDatabase.breedingDao().getAllByCattleId(cattleId)
    }

    override fun getAllBreedingWithCattle(): Flow<List<BreedingWithCattle>> {
        return fakeAppDatabase.breedingDao().getAllBreedingWithCattle()
    }

    override fun getBreedingWithCattle(breedingId: String): Flow<BreedingWithCattle?> {
        return fakeAppDatabase.breedingDao().getBreedingWithCattle(breedingId)
    }

    override fun getAllBreedingWithCattleByCattleId(
        cattleId: String
    ): Flow<List<BreedingWithCattle>> {
        return fakeAppDatabase.breedingDao().getAllBreedingWithCattleByCattleId(cattleId)
    }

    override suspend fun updateBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().update(breeding)
    }

    override suspend fun deleteBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().delete(breeding)
    }
}
