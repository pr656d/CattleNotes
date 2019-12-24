package com.pr656d.cattlenotes.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pr656d.cattlenotes.data.model.BreedingCycle
import com.pr656d.cattlenotes.data.model.Cattle

/**
 * The [Room] database for this app.
 */
@Database(entities = [
    Cattle::class,
    BreedingCycle::class
],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cattleDao(): CattleDao

    companion object {
        private const val databaseName = "cattlenotes-db"

        fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .build()
    }
}