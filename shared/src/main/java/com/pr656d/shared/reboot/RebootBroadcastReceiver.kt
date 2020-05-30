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

package com.pr656d.shared.reboot

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.work.breeding.BreedingNotificationAlarmUpdaterWorker
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

/**
 * Handles on device reboot operations.
 */
class RebootBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var authIdDataSource: AuthIdDataSource

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Timber.d("Received reboot broadcast")

        authIdDataSource.getUserId() ?: let {
            Timber.w("User id not found")
            return
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = OneTimeWorkRequestBuilder<BreedingNotificationAlarmUpdaterWorker>()
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)

            Timber.d("Worker scheduled to update all notifications")
        }
    }
}