package com.pr656d.shared.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.user.info.datasources.*
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
    fun provideUpdateFirestoreUserInfoDataSource(
        authIdDataSource: AuthIdDataSource,
        firestore: FirebaseFirestore
    ): UpdateFirestoreUserInfoDataSource {
        return UpdateFirestoreUserInfoDataSourceImpl(
            authIdDataSource,
            firestore
        )
    }

    @Singleton
    @Provides
    fun provideUpdateUserInfoBasicDataSource(
        firebaseAuth: FirebaseAuth
    ): UpdateUserInfoBasicDataSource {
        return UpdateUserInfoBasicDataSourceImpl(
            firebaseAuth
        )
    }

    @Singleton
    @Provides
    fun provideUpdateUserInfoDetailedDataSource(
        updateUserInfoBasicDataSource: UpdateUserInfoBasicDataSource,
        updateFirestoreUserInfoDataSource: UpdateFirestoreUserInfoDataSource
    ): UpdateUserInfoDetailedDataSource {
        return UpdateUserInfoDetailedDataSourceImpl(
            updateUserInfoBasicDataSource,
            updateFirestoreUserInfoDataSource
        )
    }
}