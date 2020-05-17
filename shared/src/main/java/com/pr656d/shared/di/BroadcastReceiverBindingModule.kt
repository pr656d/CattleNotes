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