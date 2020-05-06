package com.pr656d.cattlenotes.test.util.fakes

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.pr656d.shared.data.db.AppDatabase
import com.pr656d.shared.data.db.BreedingDao
import com.pr656d.shared.data.db.CattleDao
import com.pr656d.shared.data.db.MilkDao
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