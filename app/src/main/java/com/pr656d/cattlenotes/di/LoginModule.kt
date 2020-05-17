/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pr656d.shared.data.db.AppDatabaseDao
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.login.datasources.FirebaseAuthStateUserDataSource
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSource
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSourceImpl
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.fcm.FcmTokenUpdater
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoginModule {

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
        firestore: FirebaseFirestore,
        preferenceStorageRepository: PreferenceStorageRepository,
        dbLoader: DbLoader,
        breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater
    ): AuthStateUserDataSource {
        return FirebaseAuthStateUserDataSource(
            firebase,
            appDatabaseDao,
            FcmTokenUpdater(firestore),
            preferenceStorageRepository,
            dbLoader,
            breedingNotificationAlarmUpdater
        )
    }

    @Singleton
    @Provides
    fun provideObserveFirestoreUserInfoDataSource(
        firestore: FirebaseFirestore
    ): ObserveFirestoreUserInfoDataSource {
        return ObserveFirestoreUserInfoDataSourceImpl(firestore)
    }
}