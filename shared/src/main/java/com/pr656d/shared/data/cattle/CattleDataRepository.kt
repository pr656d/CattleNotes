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
package com.pr656d.shared.data.cattle

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.datasource.CattleDataSource
import com.pr656d.shared.data.db.dao.CattleDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Single point of access for [Cattle] data for the presentation layer.
 *
 * Info: Data will be loaded by data source. CRUD operations will be done on
 * Local DB as well as at data source to optimise operation count.
 */
interface CattleRepository {
    suspend fun addCattle(cattle: Cattle): Long

    suspend fun addAllCattle(cattleList: List<Cattle>): List<Long>

    /**
     * Loads data from data source and saves into db.
     */
    suspend fun load()

    fun getAllCattle(): Flow<List<Cattle>>

    fun getCattleById(id: String): Flow<Cattle?>

    fun getCattleByTagNumber(tagNumber: Long): Flow<Cattle?>

    suspend fun deleteCattle(cattle: Cattle)

    suspend fun updateCattle(cattle: Cattle)

    /** Returns cattle with matching tag number exist or not */
    suspend fun isCattleExistByTagNumber(tagNumber: Long): Boolean
}

open class CattleDataRepository @Inject constructor(
    private val cattleDao: CattleDao,
    private val cattleDataSource: CattleDataSource
) : CattleRepository {

    override suspend fun addCattle(cattle: Cattle): Long {
        cattleDataSource.addCattle(cattle)
        return cattleDao.insert(cattle)
    }

    override suspend fun addAllCattle(cattleList: List<Cattle>): List<Long> {
        cattleDataSource.addAllCattle(cattleList)
        return cattleDao.insertAll(cattleList)
    }

    override suspend fun load() {
        val list = cattleDataSource.load()
        cattleDao.insertAll(list)
    }

    override fun getAllCattle(): Flow<List<Cattle>> {
        return cattleDao.getAll()
    }

    override fun getCattleById(id: String): Flow<Cattle?> {
        return cattleDao.getById(id)
    }

    override fun getCattleByTagNumber(tagNumber: Long): Flow<Cattle?> {
        return getCattleByTagNumber(tagNumber)
    }

    override suspend fun deleteCattle(cattle: Cattle) {
        cattleDataSource.deleteCattle(cattle)
        cattleDao.delete(cattle)
    }

    override suspend fun updateCattle(cattle: Cattle) {
        cattleDataSource.updateCattle(cattle)
        cattleDao.update(cattle)
    }

    override suspend fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
        return cattleDao.getRowCountWithMatchingTagNumber(tagNumber) > 0
    }
}
