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
package com.pr656d.shared.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.pr656d.model.Breeding.BreedingEvent.Calving
import com.pr656d.model.Breeding.BreedingEvent.DryOff
import com.pr656d.model.Breeding.BreedingEvent.PregnancyCheck
import com.pr656d.model.Breeding.BreedingEvent.RepeatHeat
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.R
import com.pr656d.shared.domain.breeding.detail.GetBreedingWithCattleByIdUseCase
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.nameOrTagNumber
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Receives broadcast intents with information for breeding notifications.
 */
class BreedingAlarmBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var loadBreedingWithCattleByIdUseCase: GetBreedingWithCattleByIdUseCase

    private val alarmScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @ExperimentalCoroutinesApi
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Timber.d("Received breeding alarm.")

        val breedingId = intent.getStringExtra(EXTRA_BREEDING_ID) ?: return

        alarmScope.launch {
            val breedingWithCattle =
                getBreedingWithCattle(breedingId) ?: run {
                    Timber.d("BreedingWithCattle not found for breeding id $breedingId")
                    return@launch
                }

            try {
                showBreedingReminderNotification(context, breedingWithCattle)
            } catch (e: Exception) {
                Timber.e(e)
                return@launch
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun getBreedingWithCattle(breedingId: String): BreedingWithCattle? {
        return loadBreedingWithCattleByIdUseCase(breedingId)
            .firstOrNull()
            ?.let {
                (it as? Result.Success)?.data
            }
    }

    @WorkerThread
    @Throws(Exception::class)
    private fun showBreedingReminderNotification(
        context: Context,
        data: BreedingWithCattle
    ): Int {
        Timber.d("Showing breeding reminder notification for ${data.breeding.id}")

        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification Manager not found.")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannelForBreedingReminder(context, notificationManager)
        }

        val typeDisplayName = context.getString(
            when (val type = data.breeding.nextBreedingEvent) {
                is RepeatHeat -> R.string.repeat_heat
                is PregnancyCheck -> R.string.pregnancy_check
                is DryOff -> R.string.dry_off
                is Calving -> R.string.calving
                null -> throw IllegalStateException(
                    "Next breeding event type can not be " +
                        "$type for breeding ${data.breeding.id}"
                )
            }
        )

        val intent = Intent(
            ACTION_VIEW,
            "cattlenotes://timeline/${data.breeding.id}".toUri()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context)
            // Add the intent, which inflates the back stack
            .addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BREEDING_REMINDER)
            .setContentTitle(context.getString(R.string.breeding_reminder_notifications_title))
            .setContentText("$typeDisplayName of ${data.cattle.nameOrTagNumber()}")
            .setContentIntent(resultPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.logo) // TODO ("Logo") : Put better logo
            .setAutoCancel(true)
            .build()

        val notificationId = data.breeding.id.hashCode()
        notificationManager.notify(notificationId, notification)

        return notificationId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannelForBreedingReminder(
        context: Context,
        notificationManager: NotificationManager
    ) {
        notificationManager.createNotificationChannel(
            // Create channel
            NotificationChannel(
                CHANNEL_ID_BREEDING_REMINDER,
                context.getString(R.string.breeding_reminder_notifications),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                // Notification posted to this channel shown on lock screen.
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        )
    }

    companion object {
        const val EXTRA_BREEDING_ID = "breeding_id_extra"
        const val CHANNEL_ID_BREEDING_REMINDER = "breeding_reminder_channel_id"
        const val QUERY_BREEDING_ID = "breeding_id"
    }
}
