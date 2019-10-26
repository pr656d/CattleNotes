package com.pr656d.cattlenotes.di.component

import com.pr656d.cattlenotes.di.ActivityScope
import com.pr656d.cattlenotes.di.module.ActivityModule
import com.pr656d.cattlenotes.ui.login_signup.LoginActivity
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.ui.splash.SplashActivity
import dagger.Component

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(activity: SplashActivity)

    fun inject(activity: MainActivity)

    fun inject(activity: LoginActivity)
}