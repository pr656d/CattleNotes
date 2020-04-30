package com.pr656d.shared.di

import com.pr656d.shared.notifications.BreedingAlarmBroadcastReceiver
import com.pr656d.shared.reboot.RebootBroadcastReceiver
import com.pr656d.shared.sms.SmsBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module where all BroadcastReceivers are defined.
 */
@Module
@Suppress("UNUSED")
abstract class BroadcastReceiverBindingModule {
    @BroadcastReceiverScoped
    @ContributesAndroidInjector
    internal abstract fun breedingAlarmBroadcastReceiver(): BreedingAlarmBroadcastReceiver

    @BroadcastReceiverScoped
    @ContributesAndroidInjector
    internal abstract fun breedingRebootBroadcastReceiver(): RebootBroadcastReceiver

    @BroadcastReceiverScoped
    @ContributesAndroidInjector
    internal abstract fun smsBroadcastReceiver(): SmsBroadcastReceiver
}