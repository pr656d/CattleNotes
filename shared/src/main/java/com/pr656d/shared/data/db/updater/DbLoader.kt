/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.data.db.updater

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.pr656d.shared.R
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.db.dao.AppDatabaseDao
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.di.IoDispatcher
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Load data from data source and save to Local db.
 * @see DatabaseLoader
 */
interface DbLoader {
    /**
     * Initialize db loader.
     */
    fun initialize()

    /**  Load data.  */
    fun load()

    /**  Reload clears the local database and reloads from the data source.  */
    fun reload()
}

class DatabaseLoader @Inject constructor(
    private val context: Context,
    private val appDatabaseDao: AppDatabaseDao,
    private val cattleRepository: CattleRepository,
    private val breedingRepository: BreedingRepository,
    private val milkRepository: MilkRepository,
    private val preferenceStorageRepository: PreferenceStorageRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : DbLoader {

    private val dbLoaderScope = CoroutineScope(ioDispatcher + Job())

    override fun initialize() {
        dbLoaderScope.launch {
            Timber.d("Initializing DbLoader")

            preferenceStorageRepository
                .getObservableReloadData()
                .collect {
                    if (it) {
                        reload()
                        preferenceStorageRepository.setReloadData(false)
                    }
                }

            Timber.d("Initialized DbLoader")
        }
    }

    override fun load() {
        dbLoaderScope.launch {
            // Show progress in notification
            showProgressNotification(context)

            val task1And2 = async {
                /** Task 1 : Load cattle data */
                cattleRepository.load()

                /**
                 * Task 2 : Load breeding data.
                 * Breeding data has foreign key to cattle data so wait for cattle data to be completed.
                 */
                breedingRepository.load()
            }

            val task3 = async {
                /**  Task 3 : Load milk data.  */
                milkRepository.load()
            }

            task1And2.await()
            task3.await()

            // Cancel progress notification
            cancelProgressNotification(context)
        }
    }

    override fun reload() {
        dbLoaderScope.launch {
            Timber.d("Executing DbLoader.reload()")
            // Clear local database.
            appDatabaseDao.clear()
            // Reload the data.
            load()
        }
    }

    private fun showProgressNotification(context: Context) {
        Timber.d("Showing loading notification")

        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification manager not found")

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            makeNotificationChannelForProgress(context, notificationManager)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_RESTORING_DATA_PROGRESS)
            .setContentTitle(context.getString(R.string.restoring_data_notifications_title))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.logo) // TODO ("Logo") : Put better logo
            .setProgress(0, 0, true)
            .setOngoing(true)
            .build()

        notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
    }

    private fun cancelProgressNotification(context: Context) {
        Timber.d("Cancelling progress notification")

        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification manager not found")

        notificationManager.cancel(PROGRESS_NOTIFICATION_ID)
    }

    @RequiresApi(VERSION_CODES.O)
    private fun makeNotificationChannelForProgress(
        context: Context,
        notificationManager: NotificationManager
    ) {
        notificationManager.createNotificationChannel(
            // Create channel
            NotificationChannel(
                CHANNEL_ID_RESTORING_DATA_PROGRESS,
                context.getString(R.string.restoring_data_notifications),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                // Notification posted to this channel shown on lock screen.
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                // Notification posted to this channel would not display light.
                enableLights(false)
                // Notification posted to this channel would not vibrate.
                enableVibration(false)
            }
        )
    }

    companion object {
        private const val CHANNEL_ID_RESTORING_DATA_PROGRESS = "restoring_data_progress_channel_id"
        private const val PROGRESS_NOTIFICATION_ID = 2
    }
}
