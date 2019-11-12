package com.pr656d.cattlenotes.ui.login

import com.firebase.ui.auth.AuthUI
import com.pr656d.cattlenotes.shared.di.ActivityScoped
import dagger.Module
import dagger.Provides

@Module
@Suppress("UNUSED")
class LoginModule {

    @ActivityScoped
    @Provides
    fun provideAuthUI(): AuthUI = AuthUI.getInstance()
}