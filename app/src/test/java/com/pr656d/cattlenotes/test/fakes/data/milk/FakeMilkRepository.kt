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
package com.pr656d.cattlenotes.test.fakes.data.milk

import com.pr656d.cattlenotes.test.fakes.data.FakeAppDatabase
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import kotlinx.coroutines.flow.Flow

open class FakeMilkRepository(
    private val fakeAppDatabase: FakeAppDatabase = FakeAppDatabase(),
    private val fakeMilkDataSourceFromSms: MilkDataSourceFromSms = FakeMilkDataSourceFromSms()
) : MilkRepository {

    override suspend fun addMilk(milk: Milk): Long {
        return fakeAppDatabase.milkDao().insert(milk)
    }

    override suspend fun addAllMilk(milkList: List<Milk>): List<Long> {
        return fakeAppDatabase.milkDao().insertAll(milkList)
    }

    override suspend fun load() {}

    override fun getAllMilk(): Flow<List<Milk>> {
        return fakeAppDatabase.milkDao().getAll()
    }

    override fun getMilkById(id: String): Flow<Milk?> {
        return fakeAppDatabase.milkDao().getById(id)
    }

    override suspend fun updateMilk(milk: Milk) {
        return fakeAppDatabase.milkDao().update(milk)
    }

    override suspend fun deleteMilk(milk: Milk) {
        return fakeAppDatabase.milkDao().delete(milk)
    }

    override suspend fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> {
        return fakeMilkDataSourceFromSms.getAllMilk(smsSource)
    }
}
