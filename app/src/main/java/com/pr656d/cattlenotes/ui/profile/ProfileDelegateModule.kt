package com.pr656d.cattlenotes.ui.profile

import dagger.Binds
import dagger.Module

@Module
@Suppress("UNUSED")
abstract class ProfileDelegateModule {

    @Binds
    abstract fun provideProfileDelegate(
        impl: ProfileDelegateImp
    ) : ProfileDelegate

}