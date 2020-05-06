package com.pr656d.shared.data.db

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.model.Milk
import com.pr656d.shared.data.prefs.SharedPreferenceStorage
import com.pr656d.shared.domain.internal.DefaultScheduler
import timber.log.Timber

/**
 * The [Room] database for this app.
 */
@Database(
    entities = [
        Cattle::class,
        Breeding::class,
        Milk::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cattleDao(): CattleDao
    abstract fun breedingDao(): BreedingDao
    abstract fun milkDao(): MilkDao

    companion object {
        private const val databaseName = "cattlenotes-db"

        /**
         * As we just don't want to deal with Migrations at all. Destructive migration is used.
         * Notify about destructive migration through [SharedPreferenceStorage].
         * https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
         */
        fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)

                        Timber.d("falling back to destructive migration")

                        DefaultScheduler.execute {
                            notifyAboutDestructiveMigration(context)
                        }
                    }
                })
                .build()

        @WorkerThread
        private fun notifyAboutDestructiveMigration(context: Context) {
            Timber.d("Notifying about destructive migration")

            context.applicationContext
                .getSharedPreferences(
                    SharedPreferenceStorage.PREFS_NAME,
                    Context.MODE_PRIVATE
                ).edit {
                    putBoolean(SharedPreferenceStorage.PREF_RELOAD_DATA, true)
                }
        }
    }
}