package com.pr656d.shared.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.breeding.datasource.BreedingDataSource
import com.pr656d.shared.data.breeding.datasource.FirestoreBreedingDataSource
import com.pr656d.shared.data.cattle.datasource.CattleDataSource
import com.pr656d.shared.data.cattle.datasource.FirestoreCattleDataSource
import com.pr656d.shared.data.db.BreedingDao
import com.pr656d.shared.data.db.CattleDao
import com.pr656d.shared.data.db.MilkDao
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.milk.datasource.FirestoreMilkDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSmsImpl
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdaterImp
import com.pr656d.shared.notifications.BreedingAlarmManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module where classes created in the shared module are created.
 */
@Module
class SharedModule {
    @Singleton
    @Provides
    fun provideFirebaseFireStore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            // This is to enable the offline data
            // https://firebase.google.com/docs/firestore/manage-data/enable-offline
            .setPersistenceEnabled(true)
            .build()
        return firestore
    }

    @Singleton
    @Provides
    fun provideCattleDataSource(
        authIdDataSource: AuthIdDataSource,
        firestore: FirebaseFirestore,
        cattleDao: CattleDao
    ): CattleDataSource {
        return FirestoreCattleDataSource(authIdDataSource, firestore, cattleDao)
    }

    @Singleton
    @Provides
    fun provideBreedingDataSource(
        authIdDataSource: AuthIdDataSource,
        firestore: FirebaseFirestore,
        breedingDao: BreedingDao
    ) : BreedingDataSource {
        return FirestoreBreedingDataSource(authIdDataSource, firestore, breedingDao)
    }

    @Singleton
    @Provides
    fun provideBreedingNotificationAlarmUpdater(
        breedingAlarmManager: BreedingAlarmManager,
        breedingRepository: BreedingRepository,
        preferenceStorageRepository: PreferenceStorageRepository
    ) : BreedingNotificationAlarmUpdater {
        return BreedingNotificationAlarmUpdaterImp(
            breedingAlarmManager, breedingRepository, preferenceStorageRepository
        )
    }

    @Singleton
    @Provides
    fun provideMilkDataSource(
        authIdDataSource: AuthIdDataSource,
        firestore: FirebaseFirestore,
        milkDao: MilkDao
    ) : MilkDataSource {
        return FirestoreMilkDataSource(authIdDataSource, firestore, milkDao)
    }

    @Singleton
    @Provides
    fun provideMilkDataSourceFromSms(
        context: Context
    ) : MilkDataSourceFromSms {
        return MilkDataSourceFromSmsImpl(context)
    }
}