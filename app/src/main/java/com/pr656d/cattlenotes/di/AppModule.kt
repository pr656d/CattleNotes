package com.pr656d.cattlenotes.di

import android.content.Context
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.shared.analytics.AnalyticsHelper
import com.pr656d.shared.data.breeding.BreedingDataRepository
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.breeding.datasource.BreedingDataSource
import com.pr656d.shared.data.cattle.CattleDataRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.cattle.datasource.CattleDataSource
import com.pr656d.shared.data.db.*
import com.pr656d.shared.data.db.updater.DatabaseLoader
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.milk.MilkDataRepository
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.milk.datasource.MilkDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.prefs.SharedPreferenceStorageRepository
import com.pr656d.shared.data.prefs.datasource.PreferenceStorage
import com.pr656d.shared.data.prefs.datasource.SharedPreferenceStorage
import com.pr656d.shared.utils.FirebaseAnalyticsHelper
import com.pr656d.shared.utils.NetworkHelper
import com.pr656d.shared.utils.NetworkHelperImpl
import dagger.Module
import dagger.Provides
import timber.log.Timber
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
    fun providePreferenceStorage(context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)

    @Singleton
    @Provides
    fun providePreferenceStorageRepository(
        preferenceStorage: PreferenceStorage
    ): PreferenceStorageRepository = SharedPreferenceStorageRepository(preferenceStorage)

    @Singleton
    @Provides
    fun provideFirebaseAnalyticsHelper(
        context: Context
    ): AnalyticsHelper = FirebaseAnalyticsHelper(context)

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCattleDao(appDatabase: AppDatabase): CattleDao = appDatabase.cattleDao()

    @Singleton
    @Provides
    fun provideBreedingDao(appDatabase: AppDatabase): BreedingDao = appDatabase.breedingDao()

    @Singleton
    @Provides
    fun provideMilkDao(appDatabase: AppDatabase): MilkDao = appDatabase.milkDao()

    @Singleton
    @Provides
    fun provideAppDatabaseDao(appDatabase: AppDatabase): AppDatabaseDao {
        return object : AppDatabaseDao {
            override fun clear() {
                Timber.d("Clearing app database")
                appDatabase.clearAllTables()
                Timber.d("Cleared app database")
            }
        }
    }

    @Singleton
    @Provides
    fun provideCattleRepository(
        cattleDao: CattleDao,
        cattleDataSource: CattleDataSource
    ): CattleRepository {
        return CattleDataRepository(cattleDao, cattleDataSource)
    }

    @Singleton
    @Provides
    fun provideBreedingRepository(
        breedingDao: BreedingDao,
        breedingDataSource: BreedingDataSource
    ): BreedingRepository {
        return BreedingDataRepository(breedingDao, breedingDataSource)
    }

    @Singleton
    @Provides
    fun provideMilkRepository(
        milkDao: MilkDao,
        milkDataSource: MilkDataSource,
        milkDataSourceFromSms: MilkDataSourceFromSms
    ): MilkRepository {
        return MilkDataRepository(milkDao, milkDataSource, milkDataSourceFromSms)
    }

    @Singleton
    @Provides
    fun provideDbUpdater(
        appDatabaseDao: AppDatabaseDao,
        cattleDataSource: CattleDataSource,
        breedingDataSource: BreedingDataSource,
        milkDataSource: MilkDataSource,
        context: Context,
        preferenceStorageRepository: PreferenceStorageRepository
    ): DbLoader {
        return DatabaseLoader(
            appDatabaseDao,
            cattleDataSource,
            breedingDataSource,
            milkDataSource,
            context,
            preferenceStorageRepository
        )
    }

    @Singleton
    @Provides
    fun provideNetworkHelper(
        context: Context
    ): NetworkHelper {
        return NetworkHelperImpl(context)
    }
}
