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
package com.pr656d.shared.data.breeding

import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.datasource.BreedingDataSource
import com.pr656d.shared.data.db.dao.BreedingDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Breeding] data for the presentation layer.
 *
 * Info: Data will be loaded by data source. CRUD operations will be done on
 * Local DB as well as at data source to optimise operation count.
 */
interface BreedingRepository {
    suspend fun addBreeding(breeding: Breeding): Long

    suspend fun addAllBreeding(breedingList: List<Breeding>): List<Long>

    /**
     * Loads data from data source and saves into db.
     */
    suspend fun load()

    fun getAllBreeding(): Flow<List<Breeding>>

    fun getBreedingById(breedingId: String): Flow<Breeding?>

    fun getAllBreedingByCattleId(cattleId: String): Flow<List<Breeding>>

    fun getAllBreedingWithCattle(): Flow<List<BreedingWithCattle>>

    fun getBreedingWithCattle(breedingId: String): Flow<BreedingWithCattle?>

    fun getAllBreedingWithCattleByCattleId(cattleId: String): Flow<List<BreedingWithCattle>>

    suspend fun updateBreeding(breeding: Breeding)

    suspend fun deleteBreeding(breeding: Breeding)
}

@Singleton
open class BreedingDataRepository @Inject constructor(
    private val breedingDao: BreedingDao,
    private val breedingDataSource: BreedingDataSource
) : BreedingRepository {
    override suspend fun addBreeding(breeding: Breeding): Long {
        breedingDataSource.addBreeding(breeding)
        return breedingDao.insert(breeding)
    }

    override suspend fun addAllBreeding(breedingList: List<Breeding>): List<Long> {
        breedingDataSource.addAllBreeding(breedingList)
        return breedingDao.insertAll(breedingList)
    }

    override suspend fun load() {
        val list = breedingDataSource.load()
        breedingDao.insertAll(list)
    }

    override fun getAllBreeding(): Flow<List<Breeding>> {
        return breedingDao.getAll()
    }

    override fun getBreedingById(breedingId: String): Flow<Breeding?> {
        return breedingDao.getById(breedingId)
    }

    override fun getAllBreedingByCattleId(cattleId: String): Flow<List<Breeding>> {
        return breedingDao.getAllByCattleId(cattleId)
    }

    override fun getAllBreedingWithCattle(): Flow<List<BreedingWithCattle>> {
        return breedingDao.getAllBreedingWithCattle()
    }

    override fun getBreedingWithCattle(breedingId: String): Flow<BreedingWithCattle?> {
        return breedingDao.getBreedingWithCattle(breedingId)
    }

    override fun getAllBreedingWithCattleByCattleId(
        cattleId: String
    ): Flow<List<BreedingWithCattle>> {
        return breedingDao.getAllBreedingWithCattleByCattleId(cattleId)
    }

    override suspend fun updateBreeding(breeding: Breeding) {
        breedingDataSource.updateBreeding(breeding)
        return breedingDao.update(breeding)
    }

    override suspend fun deleteBreeding(breeding: Breeding) {
        breedingDataSource.deleteBreeding(breeding)
        return breedingDao.delete(breeding)
    }
}
