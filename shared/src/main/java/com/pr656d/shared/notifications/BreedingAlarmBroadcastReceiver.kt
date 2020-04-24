package com.pr656d.shared.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.R
import com.pr656d.shared.domain.breeding.detail.GetBreedingWithCattleByIdUseCase
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.utils.nameOrTagNumber
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

/**
 * Receives broadcast intents with information for breeding notifications.
 */
class BreedingAlarmBroadcastReceiver : DaggerBroadcastReceiver() {
    @Inject lateinit var loadBreedingWithCattleByIdUseCase: GetBreedingWithCattleByIdUseCase

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Timber.d("Received breeding alarm.")

        val breedingId = intent.getStringExtra(EXTRA_BREEDING_ID) ?: return

        loadBreedingWithCattleByIdUseCase(breedingId).let {
            val observer = object : Observer<BreedingWithCattle?> {
                override fun onChanged(breedingWithCattle: BreedingWithCattle?) {
                    it.removeObserver(this)

                    breedingWithCattle ?: run {
                        Timber.d("BreedingWithCattle not found for breeding id $breedingId")
                        return
                    }

                    DefaultScheduler.execute {
                        try {
                            showBreedingReminderNotification(context, breedingWithCattle)
                        } catch (e: Exception) {
                            Timber.e(e)
                            return@execute
                        }
                    }
                }
            }

            it.observeForever(observer)
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
            when (val type = data.breeding.nextBreedingEvent?.type) {
                Breeding.BreedingEvent.Type.REPEAT_HEAT -> R.string.repeat_heat
                Breeding.BreedingEvent.Type.PREGNANCY_CHECK -> R.string.pregnancy_check
                Breeding.BreedingEvent.Type.DRY_OFF -> R.string.dry_off
                Breeding.BreedingEvent.Type.CALVING -> R.string.calving
                else -> throw IllegalStateException("Next breeding event type can not be ${type?.displayName} of breeding ${data.breeding.id}")
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
            .setSmallIcon(R.drawable.logo)  // TODO ("Logo") : Put better logo
            .setAutoCancel(true)
            .build()

        val notificationId = data.breeding.id.hashCode()
        notificationManager.notify(notificationId, notification)

        return notificationId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannelForBreedingReminder(
        context: Context, notificationManager: NotificationManager
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