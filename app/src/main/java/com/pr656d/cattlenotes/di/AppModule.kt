package com.pr656d.cattlenotes.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.shared.data.breeding.BreedingDataRepository
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.cattle.CattleDataRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.db.AppDatabase
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.data.prefs.SharedPreferenceStorage
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
    fun provideContext(application: CattleNotesApplication): Context = application.applicationContext

    @Provides
    fun provideFirebaseAuthUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    @Singleton
    @Provides
    fun providesPreferenceStorage(context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)

    @Singleton
    @Provides
    fun providesAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCattleDataRepository(appDatabase: AppDatabase): CattleRepository {
        return CattleDataRepository(
            appDatabase
        )
    }

    @Singleton
    @Provides
    fun provideBreedingRepository(appDatabase: AppDatabase): BreedingRepository {
        return BreedingDataRepository(
            appDatabase
        )
    }

}
