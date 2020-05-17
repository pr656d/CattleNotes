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

package com.pr656d.shared.data.cattle

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.datasource.CattleDataSource
import com.pr656d.shared.data.db.CattleDao
import javax.inject.Inject

/**
 * Single point of access for [Cattle] data for the presentation layer.
 *
 * Info: Data will be loaded by data source. CRUD operations will be done on
 * Local DB as well as at data source to optimise operation count.
 */
interface CattleRepository {
    fun addCattle(cattle: Cattle)

    fun getAllCattle(): LiveData<List<Cattle>>

    fun getCattleById(id: String): LiveData<Cattle?>

    fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?>

    fun deleteCattle(cattle: Cattle)

    fun updateCattle(cattle: Cattle)

    /** Returns cattle with matching tag number exist or not */
    fun isCattleExistByTagNumber(tagNumber: Long): Boolean
}

open class CattleDataRepository @Inject constructor(
    private val cattleDao: CattleDao,
    private val cattleDataSource: CattleDataSource
) : CattleRepository {

    /**
     * Add cattle at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun addCattle(cattle: Cattle) {
        cattleDao.insert(cattle)
        cattleDataSource.addCattle(cattle)
    }

    override fun getAllCattle(): LiveData<List<Cattle>> {
        return cattleDao.getAll()
    }

    override fun getCattleById(id: String): LiveData<Cattle?> {
        return cattleDao.getById(id)
    }

    override fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?> {
        return cattleDao.getByTagNumber(tagNumber)
    }

    /**
     * Delete cattle at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun deleteCattle(cattle: Cattle) {
        cattleDao.delete(cattle)
        cattleDataSource.deleteCattle(cattle)
    }

    /**
     * Update cattle at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun updateCattle(cattle: Cattle) {
        cattleDao.update(cattle)
        cattleDataSource.updateCattle(cattle)
    }

    override fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
        // If count is more than 0 than it exists.
        return cattleDao.getRowCountWithMatchingTagNumber(tagNumber) > 0
    }
}