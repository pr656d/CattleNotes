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

package com.pr656d.shared.data.milk

import com.pr656d.model.Milk
import com.pr656d.shared.data.db.dao.MilkDao
import com.pr656d.shared.data.milk.datasource.MilkDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 *
 * Single point of access for [Milk] data for the presentation layer.
 */
interface MilkRepository {

    suspend fun addMilk(milk: Milk): Long

    suspend fun addAllMilk(milkList: List<Milk>): List<Long>

    /**
     * Load from data source and save into db.
     */
    suspend fun load()

    fun getAllMilk(): Flow<List<Milk>>

    fun getMilkById(id: String): Flow<Milk?>

    suspend fun updateMilk(milk: Milk)

    suspend fun deleteMilk(milk: Milk)

    /**  Provides milk list from SMS data source.  */
    suspend fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk>
}

class MilkDataRepository @Inject constructor(
    private val milkDao: MilkDao,
    private val milkDataSource: MilkDataSource,
    private val milkDataSourceFromSms: MilkDataSourceFromSms
) : MilkRepository {

    override suspend fun addMilk(milk: Milk): Long {
        milkDataSource.addMilk(milk)
        return milkDao.insert(milk)
    }

    override suspend fun addAllMilk(milkList: List<Milk>): List<Long> {
        milkDataSource.addAllMilk(milkList)
        return milkDao.insertAll(milkList)
    }

    override suspend fun load() {
        val list = milkDataSource.load()
        milkDao.insertAll(list)
    }

    override fun getAllMilk(): Flow<List<Milk>> {
        return milkDao.getAll()
    }

    override fun getMilkById(id: String): Flow<Milk?> {
        return milkDao.getById(id)
    }

    override suspend fun updateMilk(milk: Milk) {
        milkDataSource.updateMilk(milk)
        return milkDao.update(milk)
    }

    override suspend fun deleteMilk(milk: Milk) {
        milkDataSource.deleteMilk(milk)
        return milkDao.delete(milk)
    }

    override suspend fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> {
        return milkDataSourceFromSms.getAllMilk(smsSource)
    }
}