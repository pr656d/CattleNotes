package com.pr656d.cattlenotes.ui.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.login.datasources.ObserveFirebaseUserInfoDataSource
import com.pr656d.shared.data.login.datasources.ReloadFirebaseUserInfo
import com.pr656d.shared.data.user.info.datasources.*
import com.pr656d.shared.data.user.repository.UserInfoDataRepository
import com.pr656d.shared.data.user.repository.UserInfoRepository
import com.pr656d.shared.domain.user.info.ObserveUserInfoDetailed
import com.pr656d.shared.domain.user.info.UpdateUserInfoDetailedUseCase
import com.pr656d.shared.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@Suppress("UNUSED")
class ProfileDelegateModule {

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

    @Singleton
    @Provides
    fun provideUserInfoRepository(
        updateUserInfoDetailedDataSource: UpdateUserInfoDetailedDataSource
    ) : UserInfoRepository {
        return UserInfoDataRepository(
            updateUserInfoDetailedDataSource
        )
    }

    @Singleton
    @Provides
    fun provideProfileDelegate(
        observeUserInfoDetailed: ObserveUserInfoDetailed,
        firebaseAuth: FirebaseAuth,
        updateUserInfoDetailedUseCase: UpdateUserInfoDetailedUseCase
    ) : ProfileDelegate {
        return ProfileDelegateImp (
            observeUserInfoDetailed,
            firebaseAuth,
            updateUserInfoDetailedUseCase
        )
    }

    @Singleton
    @Provides
    fun provideUpdateFirestoreUserInfoDataSource(
        authIdDataSource: AuthIdDataSource,
        firestore: FirebaseFirestore,
        networkHelper: NetworkHelper
    ): UpdateFirestoreUserInfoDataSource {
        return UpdateFirestoreUserInfoDataSourceImpl(
            authIdDataSource,
            firestore,
            networkHelper
        )
    }

    @Singleton
    @Provides
    fun provideReloadFirebaseUserInfo(
        observeFirebaseUserInfoDataSource: ObserveFirebaseUserInfoDataSource
    ) : ReloadFirebaseUserInfo {
        return observeFirebaseUserInfoDataSource
    }

    @Singleton
    @Provides
    fun provideUpdateUserInfoBasicDataSource(
        firebaseAuth: FirebaseAuth,
        reloadFirebaseUserInfo: ReloadFirebaseUserInfo
    ): UpdateUserInfoBasicDataSource {
        return UpdateUserInfoBasicDataSourceImpl(
            firebaseAuth,
            reloadFirebaseUserInfo
        )
    }
}