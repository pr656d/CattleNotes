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

import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.notifications.BreedingAlarmManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
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

    suspend fun cancelAll()

    suspend fun cancelByCattleId(cattleId: String)

    fun cancelByBreedingId(breedingId: String)
}

@Singleton
class BreedingNotificationAlarmUpdaterImp @Inject constructor(
    private val breedingAlarmManager: BreedingAlarmManager,
    private val breedingRepository: BreedingRepository,
    private val preferenceStorageRepository: PreferenceStorageRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BreedingNotificationAlarmUpdater {

    private val alarmUpdaterScope =
        CoroutineScope(defaultDispatcher + SupervisorJob())

    private var lastPreferredTimeForBreedingReminder: LocalTime? = null

    init {
        alarmUpdaterScope.launch {
            lastPreferredTimeForBreedingReminder =
                preferenceStorageRepository.getPreferredTimeOfBreedingReminder()
        }
    }

    @ExperimentalStdlibApi
    override fun updateAll(userId: String) {
        alarmUpdaterScope.launch {
            // Go through every breeding and make sure alarm is set for the notification.
            breedingRepository
                .getAllBreeding()
                .onEach { processBreedings(userId, it) }
                .flowOn(ioDispatcher)
                .collect()

            preferenceStorageRepository
                .getObservablePreferredTimeOfBreedingReminder()
                .flowOn(defaultDispatcher)
                .onEach { newTime ->
                    Timber.d("PreferredTimeOfBreedingReminder changed to $newTime")
                    // Prevent loop
                    if (lastPreferredTimeForBreedingReminder != newTime) {
                        Timber.d("Got new PreferredTimeOfBreedingReminder $newTime")
                        updateAll(userId)
                        lastPreferredTimeForBreedingReminder = newTime
                    }
                }
                .collect {
                    lastPreferredTimeForBreedingReminder = it
                }
        }
    }

    @ExperimentalStdlibApi
    private suspend fun processBreedings(userId: String, breedingList: List<Breeding>) =
        coroutineScope {
            Timber.d("Setting all the alarms of breeding for user : $userId")

            val timeTaken = measureTimeMillis {
                buildList {
                    breedingList.forEach { breeding ->
                        add(async { breedingAlarmManager.setAlarmForBreeding(breeding) })
                    }
                }.awaitAll()
            }

            Timber.d("Work finished of setting all the ${breedingList.size} alarms of breeding in $timeTaken ms")
        }

    private fun clear() {
        lastPreferredTimeForBreedingReminder = null
    }

    @ExperimentalStdlibApi
    override suspend fun cancelAll() {
        Timber.d("Cancelling all the breeding alarm")
        val list = breedingRepository.getAllBreeding().firstOrNull() ?: return
        alarmUpdaterScope.launch {
            cancelAllBreeding(list)
        }
        clear()
    }

    @ExperimentalStdlibApi
    override suspend fun cancelByCattleId(cattleId: String) {
        Timber.d("Cancelling all the breeding alarm for cattle : $cattleId")

        val list = breedingRepository.getAllBreedingByCattleId(cattleId).firstOrNull()
            ?: return

        cancelAllBreeding(list)
    }

    override fun cancelByBreedingId(breedingId: String) {
        Timber.d("Cancelling alarm for breeding : $breedingId")
        breedingAlarmManager.cancelAlarmForBreeding(breedingId)
    }

    @ExperimentalStdlibApi
    private suspend fun cancelAllBreeding(breedingList: List<Breeding>) = coroutineScope {
        Timber.d("Cancelling all the alarms of breeding")

        val timeTaken = measureTimeMillis {
            buildList {
                breedingList.forEach { breeding ->
                    add(async { breedingAlarmManager.setAlarmForBreeding(breeding) })
                }
            }.awaitAll()
        }

        Timber.d("Work finished of cancelling all the ${breedingList.size} alarms of breeding in $timeTaken ms")
    }
}