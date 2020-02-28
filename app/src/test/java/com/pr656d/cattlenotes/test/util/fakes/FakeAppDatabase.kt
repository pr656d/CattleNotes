package com.pr656d.cattlenotes.test.util.fakes

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.local.db.BreedingDao
import com.pr656d.cattlenotes.data.local.db.CattleDao
import org.mockito.Mockito

class FakeAppDatabase : AppDatabase() {

    override fun cattleDao(): CattleDao {
        return Mockito.mock(CattleDao::class.java)
    }

    override fun breedingDao(): BreedingDao {
        return Mockito.mock(BreedingDao::class.java)
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        return Mockito.mock(SupportSQLiteOpenHelper::class.java)
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return Mockito.mock(InvalidationTracker::class.java)
    }

    override fun clearAllTables() {}
}