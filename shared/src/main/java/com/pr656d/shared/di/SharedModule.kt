package com.pr656d.shared.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import com.pr656d.shared.data.cattle.datasources.CattleUpdater
import com.pr656d.shared.data.cattle.datasources.FirestoreCattleDataSource
import com.pr656d.shared.data.cattle.datasources.FirestoreCattleUpdater
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
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
        firestore: FirebaseFirestore
    ): CattleDataSource {
        return FirestoreCattleDataSource(authIdDataSource, firestore)
    }

    @Singleton
    @Provides
    fun provideCattleUpdater(
        firestore: FirebaseFirestore,
        authIdDataSource: AuthIdDataSource,
        cattleRepository: CattleRepository
    ) : CattleUpdater {
        return FirestoreCattleUpdater(
            firestore, authIdDataSource, cattleRepository
        )
    }
}