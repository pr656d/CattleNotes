package com.pr656d.shared.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle

/**
 * The [Room] database for this app.
 */
@Database(entities = [
    Cattle::class,
    Breeding::class
],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cattleDao(): CattleDao
    abstract fun breedingDao(): BreedingDao

    companion object {
        private const val databaseName = "cattlenotes-db"

        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
        fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
    }
}