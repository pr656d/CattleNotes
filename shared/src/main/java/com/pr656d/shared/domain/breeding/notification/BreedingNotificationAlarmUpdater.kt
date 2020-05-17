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

package com.pr656d.shared.domain.breeding.notification

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.notifications.BreedingAlarmManager
import org.threeten.bp.LocalTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

/**
 * Sets a notification for each breeding and observers for any changes and
 * reschedules all notifications.
 *
 * @see BreedingNotificationAlarmUpdaterImp
 */
interface BreedingNotificationAlarmUpdater {
    fun updateAll(userId: String)

    fun cancelAll(onComplete: () -> Unit = {})

    fun cancelByCattleId(cattleId: String, onComplete: () -> Unit = {})

    fun cancelByBreedingId(breedingId: String)
}

@Singleton
class BreedingNotificationAlarmUpdaterImp @Inject constructor(
    private val breedingAlarmManager: BreedingAlarmManager,
    private val breedingRepository: BreedingRepository,
    private val preferenceStorageRepository: PreferenceStorageRepository
) : BreedingNotificationAlarmUpdater {
    private var observerBreedingList: Observer<List<Breeding>>? = null
    private var breedingList: LiveData<List<Breeding>>? = null

    private var observerPreferredTimeOfBreedingReminder: Observer<LocalTime>? = null
    private var preferredTimeOfBreedingReminder: LiveData<LocalTime>? = null

    private var lastPreferredTimeForBreedingReminder: LocalTime? = null

    private var cancelBreedingListObserver: Observer<List<Breeding>>? = null
    private var cancelBreedingList: LiveData<List<Breeding>>? = null

    init {
        DefaultScheduler.execute {
            lastPreferredTimeForBreedingReminder =
                preferenceStorageRepository.getPreferredTimeOfBreedingReminder()
        }
    }

    override fun updateAll(userId: String) {
        // Go through every breeding and make sure alarm is set for the notification.
        breedingList = breedingRepository.getAllBreeding().apply {
            val newObserver = Observer<List<Breeding>> {
                DefaultScheduler.execute {
                    processBreedings(userId, it)
                }
            }

            DefaultScheduler.postToMainThread { observeForever(newObserver) }

            observerBreedingList = newObserver
        }

        // Observe for reminder time changes.
        preferredTimeOfBreedingReminder =
            preferenceStorageRepository.getObservablePreferredTimeOfBreedingReminder().apply {
                val newObserver = Observer<LocalTime> { newTime ->

                    Timber.d("PreferredTimeOfBreedingReminder changed to $newTime")

                    // Prevent loop
                    if (lastPreferredTimeForBreedingReminder != newTime) {
                        updateAll(userId)
                        Timber.d("Got new PreferredTimeOfBreedingReminder $newTime")
                        lastPreferredTimeForBreedingReminder = newTime
                    }
                }

                DefaultScheduler.postToMainThread { observeForever(newObserver) }

                observerPreferredTimeOfBreedingReminder = newObserver
            }
    }

    @WorkerThread
    private fun processBreedings(userId: String, breedingList: List<Breeding>) {
        Timber.d("Setting all the alarms of breeding for user : $userId")

        val timeTaken = measureTimeMillis {
            breedingList.forEach { breeding ->
                breedingAlarmManager.setAlarmForBreeding(breeding)
            }
        }

        Timber.d("Work finished of setting all the alarms of breeding in $timeTaken ms")
    }

    private fun clear() {
        observerBreedingList?.let {
            breedingList?.removeObserver(it)
        }

        cancelBreedingListObserver?.let {
            cancelBreedingList?.removeObserver(it)
        }

        observerPreferredTimeOfBreedingReminder?.let {
            preferredTimeOfBreedingReminder?.removeObserver(it)
        }

        observerBreedingList = null
        breedingList = null

        cancelBreedingListObserver = null
        cancelBreedingList = null

        observerPreferredTimeOfBreedingReminder = null
        lastPreferredTimeForBreedingReminder = null
        preferredTimeOfBreedingReminder = null
    }

    override fun cancelAll(onComplete: () -> Unit) {
        Timber.d("Cancelling all the breeding alarm")

        val newObserver = Observer<List<Breeding>> {
            DefaultScheduler.execute {
                cancelAllBreeding(it)
                Timber.d("Cancelled all the breeding alarm")
            }
            onComplete()
            clear()
        }

        cancelBreedingList = breedingRepository.getAllBreeding().apply {
            DefaultScheduler.postToMainThread { observeForever(newObserver) }
        }

        cancelBreedingListObserver = newObserver
    }

    override fun cancelByCattleId(cattleId: String, onComplete: () -> Unit) {
        Timber.d("Cancelling all the breeding alarm for cattle : $cattleId")

        breedingRepository.getAllBreedingByCattleId(cattleId).apply {
            val newObserver = object : Observer<List<Breeding>> {
                override fun onChanged(list: List<Breeding>?) {
                    this@apply.removeObserver(this)

                    list?.let {
                        DefaultScheduler.execute { cancelAllBreeding(list) }
                        onComplete()
                        return
                    }

                    Timber.d("No breeding list found for cattle : $cattleId to cancel alarm")
                }
            }

            DefaultScheduler.postToMainThread { observeForever(newObserver) }
        }
    }

    override fun cancelByBreedingId(breedingId: String) {
        Timber.d("Cancelling alarm for breeding : $breedingId")
        breedingAlarmManager.cancelAlarmForBreeding(breedingId)
    }

    private fun cancelAllBreeding(breedingList: List<Breeding>) {
        breedingList.forEach { breeding ->
            breedingAlarmManager.cancelAlarmForBreeding(breeding.id)
        }
    }
}