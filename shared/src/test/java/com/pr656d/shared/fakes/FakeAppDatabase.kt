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

package com.pr656d.shared.fakes

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.pr656d.shared.data.db.AppDatabase
import com.pr656d.shared.data.db.dao.BreedingDao
import com.pr656d.shared.data.db.dao.CattleDao
import com.pr656d.shared.data.db.dao.MilkDao
import org.mockito.Mockito

class FakeAppDatabase : AppDatabase() {
    override fun cattleDao(): CattleDao {
        return Mockito.mock(CattleDao::class.java)
    }

    override fun breedingDao(): BreedingDao {
        return Mockito.mock(BreedingDao::class.java)
    }

    override fun milkDao(): MilkDao {
        return Mockito.mock(MilkDao::class.java)
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        return Mockito.mock(SupportSQLiteOpenHelper::class.java)
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return Mockito.mock(InvalidationTracker::class.java)
    }

    override fun clearAllTables() {}
}