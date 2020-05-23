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

package com.pr656d.shared.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import com.pr656d.model.Breeding
import com.pr656d.shared.data.prefs.datasource.SharedPreferenceStorage.Companion.DEFAULT_REMINDER_TIME
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.settings.GetPreferredTimeOfBreedingReminderUseCase
import com.pr656d.shared.notifications.BreedingAlarmBroadcastReceiver.Companion.EXTRA_BREEDING_ID
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.toEpochMilli
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages setting and cancelling alarms for breeding's next breeding event.
 */
open class BreedingAlarmManager @Inject constructor(val context: Context) {

    @Inject lateinit var getPreferredTimeOfBreedingReminderUseCase: GetPreferredTimeOfBreedingReminderUseCase

    private val systemAlarmManager: AlarmManager? = context.getSystemService()

    /**
     * Schedule an alarm for a breeding.
     */
    suspend fun setAlarmForBreeding(breeding: Breeding) {
        val breedingEvent = breeding.nextBreedingEvent

        // Cancel alarm if any.
        cancelAlarmForBreeding(breeding.id)

        breedingEvent ?: run {
            Timber.d("""Trying to set alarm for null breeding event for breeding ${breeding.id}, 
                |Ignoring, May be it is completed(${breeding.breedingCompleted}) breeding."""
                .trimIndent()
            )
            return
        }

        val preferredTimeForBreedingReminder = getPreferredTimeOfBreedingReminder()

        val isPastBreedingEvent = TimeUtils
            .toZonedDateTime(breedingEvent.expectedOn, preferredTimeForBreedingReminder)
            .isBefore(ZonedDateTime.now(ZoneId.systemDefault()))

        if (isPastBreedingEvent) {
            Timber.d("Trying to schedule alarm for past breeding event for breeding ${breeding.id}, Ignoring.")
            return
        }

        makePendingIntent(breeding.id)?.let {
            scheduleAlarmForBreeding(it, breeding, preferredTimeForBreedingReminder)
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun getPreferredTimeOfBreedingReminder(): LocalTime {
        return getPreferredTimeOfBreedingReminderUseCase(Unit).let {
            (it as? Result.Success)?.data ?: TimeUtils.toLocalTime(DEFAULT_REMINDER_TIME)
        }
    }

    fun cancelAlarmForBreeding(breedingId: String) {
        makePendingIntent(breedingId)?.let {
            cancelAlarmFor(it)
            Timber.d("Cancelled breeding alarm for breeding $breedingId")
        }
    }

    private fun makePendingIntent(breedingId: String): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            breedingId.hashCode(),
            Intent(context, BreedingAlarmBroadcastReceiver::class.java)
                .putExtra(EXTRA_BREEDING_ID, breedingId),
            FLAG_UPDATE_CURRENT
        )
    }

    private fun cancelAlarmFor(pendingIntent: PendingIntent) {
        try {
            systemAlarmManager?.cancel(pendingIntent)
        } catch (ex: Exception) {
            Timber.e("Couldn't cancel alarm for breeding")
        }
    }

    private fun scheduleAlarmForBreeding(
        pendingIntent: PendingIntent,
        breeding: Breeding,
        preferredTimeForBreedingReminder: LocalTime
    ) {
        val triggerAtMillis = TimeUtils
            .toZonedDateTime(breeding.nextBreedingEvent!!.expectedOn, preferredTimeForBreedingReminder)
            .toEpochMilli()
        scheduleAlarmFor(pendingIntent, breeding, triggerAtMillis)
    }

    private fun scheduleAlarmFor(
        pendingIntent: PendingIntent,
        breeding: Breeding,
        triggerAtMillis: Long
    ) {
        systemAlarmManager?.let {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                systemAlarmManager,
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            Timber.d("""Scheduled alarm for breeding ${breeding.id} at $triggerAtMillis
                |for breeding event type : ${breeding.nextBreedingEvent}"""
                .trimMargin()
            )
        }
    }
}