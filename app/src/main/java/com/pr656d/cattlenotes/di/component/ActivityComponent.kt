package com.pr656d.cattlenotes.di.component

import com.pr656d.cattlenotes.di.ActivityScope
import com.pr656d.cattlenotes.di.module.ActivityModule
import com.pr656d.cattlenotes.ui.main.MainActivity
import dagger.Component

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(activity: MainActivity)
}