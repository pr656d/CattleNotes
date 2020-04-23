package com.pr656d.shared.di

import com.pr656d.shared.notifications.BreedingAlarmBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverBindingModule {
    @BroadcastReceiverScoped
    @ContributesAndroidInjector
    internal abstract fun breedingAlarmBroadcastReceiver(): BreedingAlarmBroadcastReceiver
}