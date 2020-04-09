package com.pr656d.cattlenotes.di

import android.content.Context
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.shared.analytics.AnalyticsHelper
import com.pr656d.shared.data.breeding.BreedingDataRepository
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.breeding.BreedingUpdater
import com.pr656d.shared.data.breeding.datasources.BreedingDataSource
import com.pr656d.shared.data.cattle.CattleDataRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.cattle.CattleUpdater
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import com.pr656d.shared.data.db.AppDatabase
import com.pr656d.shared.data.db.AppDatabaseDao
import com.pr656d.shared.data.db.updater.DatabaseUpdater
import com.pr656d.shared.data.db.updater.DbUpdater
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.data.prefs.SharedPreferenceStorage
import com.pr656d.shared.utils.FirebaseAnalyticsHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 *
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
class AppModule {

    @Provides
    fun provideContext(application: CattleNotesApplication): Context =
        application.applicationContext

    @Singleton
    @Provides
    fun providesPreferenceStorage(context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)

    @Singleton
    @Provides
    fun provideFirebaseAnalyticsHelper(
        context: Context
    ): AnalyticsHelper = FirebaseAnalyticsHelper(context)

    @Singleton
    @Provides
    fun providesAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideAppDatabaseDao(appDatabase: AppDatabase) : AppDatabaseDao {
        return object : AppDatabaseDao {
            override fun clearDatabase() {
                appDatabase.clearAllTables()
            }
        }
    }

    @Singleton
    @Provides
    fun provideCattleRepository(
        appDatabase: AppDatabase,
        cattleDataSource: CattleDataSource
    ): CattleRepository {
        return CattleDataRepository(
            appDatabase,
            cattleDataSource
        )
    }

    @Singleton
    @Provides
    fun provideBreedingRepository(
        appDatabase: AppDatabase,
        breedingDataSource: BreedingDataSource
    ): BreedingRepository {
        return BreedingDataRepository(
            appDatabase,
            breedingDataSource
        )
    }

    @Singleton
    @Provides
    fun provideDbUpdater(
        cattleUpdater: CattleUpdater,
        breedingUpdater: BreedingUpdater
    ) : DbUpdater {
        return DatabaseUpdater(cattleUpdater, breedingUpdater)
    }
}
