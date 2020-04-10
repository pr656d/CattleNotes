package com.pr656d.cattlenotes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pr656d.shared.data.db.AppDatabaseDao
import com.pr656d.shared.data.db.updater.DbUpdater
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.login.datasources.FirebaseAuthStateUserDataSource
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSource
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSourceImpl
import com.pr656d.shared.fcm.FcmTokenUpdater
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SignInModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun providesAuthIdDataSource(
        firebaseAuth: FirebaseAuth
    ): AuthIdDataSource {
        return object : AuthIdDataSource {
            override fun getUserId() = firebaseAuth.currentUser?.uid
        }
    }

    @Singleton
    @Provides
    fun provideAuthStateUserDataSource(
        firebase: FirebaseAuth,
        appDatabaseDao: AppDatabaseDao,
        dbUpdater: DbUpdater,
        firestore: FirebaseFirestore,
        preferenceStorage: PreferenceStorage
    ): AuthStateUserDataSource {
        return FirebaseAuthStateUserDataSource(
            firebase,
            appDatabaseDao,
            dbUpdater,
            FcmTokenUpdater(firestore),
            preferenceStorage
        )
    }

    @Singleton
    @Provides
    fun provideObserveFirestoreUserInfoDataSource(
        firestore: FirebaseFirestore
    ): ObserveFirestoreUserInfoDataSource {
        return ObserveFirestoreUserInfoDataSourceImpl(
            firestore
        )
    }
}