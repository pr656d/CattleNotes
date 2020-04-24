package com.pr656d.shared.reboot

import android.content.Context
import android.content.Intent
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

/**
 * Handles on device reboot operations.
 */
class RebootBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater

    @Inject lateinit var authIdDataSource: AuthIdDataSource

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Timber.d("Received reboot broadcast")

        val userId = authIdDataSource.getUserId() ?: let {
            Timber.w("User id not found")
            return
        }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Resetting all the breeding alarm")
            breedingNotificationAlarmUpdater.updateAll(userId)
        }
    }
}