package com.pr656d.shared.data.db.updater

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pr656d.shared.R
import com.pr656d.shared.data.breeding.datasources.BreedingDataSource
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.internal.DefaultScheduler
import timber.log.Timber
import javax.inject.Inject

/**
 * Load data from data source and save to Local db.
 */
interface DbLoader {
    /**
     * Initialize db loader.
     */
    fun initialize()

    /**
     * Load data.
     */
    fun load(onComplete: () -> Unit = {})

    /**
     * Name convenience wrapper over load.
     */
    fun reload(onComplete: () -> Unit = {})

    /**
     * Stop db loader.
     */
    fun stop()
}

class DatabaseLoader @Inject constructor(
    private val cattleDataSource: CattleDataSource,
    private val breedingDataSource: BreedingDataSource,
    private val context: Context,
    private val preferenceStorage: PreferenceStorage
) : DbLoader {

    private var tasksCompletedCounter: MutableLiveData<Int>? = null

    private val reloadObserver = Observer<Boolean> {
        if (it == true) {
            reload(onComplete = {
                DefaultScheduler.execute {
                    preferenceStorage.reloadData = false
                }
            })
        }
    }

    override fun initialize() {
        Timber.d("Initializing DbLoader")

        tasksCompletedCounter = MutableLiveData(0)
        DefaultScheduler.postToMainThread {
            preferenceStorage.observeReloadData.observeForever(reloadObserver)
        }

        Timber.d("Initialized DbLoader")

    }

    override fun load(onComplete: () -> Unit) {
        DefaultScheduler.postToMainThread {
            initializeTaskCompletedTracker(onComplete)
        }

        DefaultScheduler.execute {
            // Show progress in notification
            showProgressNotification(context)
        }

        /** Task 1 : Load cattle data */
        cattleDataSource.load(onComplete = {
            notifyTaskCompleted()

            /**
             * Task 2 : Load breeding data.
             * Breeding data has foreign key to cattle data so wait for cattle data to be completed.
             */
            breedingDataSource.load(onComplete = {
                notifyTaskCompleted()
            })
        })
    }

    override fun reload(onComplete: () -> Unit) {
        Timber.d("Executing DbLoader.reload()")

        load(onComplete)
    }

    override fun stop() {
        Timber.d("Stopping DbLoader")

        tasksCompletedCounter = null
        DefaultScheduler.postToMainThread {
            preferenceStorage.observeReloadData.removeObserver(reloadObserver)
        }

        Timber.d("Stopped DbLoader")
    }

    @MainThread
    private fun initializeTaskCompletedTracker(onComplete: () -> Unit) {
        tasksCompletedCounter?.observeForever(
            object : Observer<Int> {
                override fun onChanged(count: Int?) {
                    Timber.d("DbLoader tasks completed : $count")

                    /** When task count reaches to [TOTAL_TASKS] all tasks are completed. */
                    if (count ?: 0 == TOTAL_TASKS) {
                        DefaultScheduler.execute {
                            // Execute on complete tasks.
                            onComplete()
                            // Cancel progress notification.
                            cancelProgressNotification(context)
                        }
                        // Reset counter
                        resetTaskCompleteCounter()
                        // Remove observer
                        tasksCompletedCounter?.removeObserver(this)
                    }
                }
            }
        )
    }

    private fun notifyTaskCompleted() {
        tasksCompletedCounter?.postValue(tasksCompletedCounter?.value?.plus(1))
    }

    @WorkerThread
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
            .setSmallIcon(R.drawable.logo)  // TODO ("Logo") : Put better logo
            .setProgress(0, 0, true)
            .setOngoing(true)
            .build()

        notificationManager.notify(PROGRESS_NOTIFICATION_ID, notification)
    }

    @WorkerThread
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

    private fun resetTaskCompleteCounter() {
        tasksCompletedCounter?.postValue(0)
    }

    companion object {
        private const val CHANNEL_ID_RESTORING_DATA_PROGRESS = "restoring_data_progress_channel_id"
        private const val PROGRESS_NOTIFICATION_ID = 2

        /**
         * Total tasks to complete.
         *      1. Load cattle data
         *      2. Load Breeding data
         */
        private const val TOTAL_TASKS: Int = 2
    }
}