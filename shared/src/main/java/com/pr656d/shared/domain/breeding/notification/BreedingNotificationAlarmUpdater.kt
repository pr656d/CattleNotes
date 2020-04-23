package com.pr656d.shared.domain.breeding.notification

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.notifications.BreedingAlarmManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

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
    private val breedingRepository: BreedingRepository
) : BreedingNotificationAlarmUpdater {
    var observerBreedingList: (Observer<List<Breeding>>)? = null
    var breedingList: LiveData<List<Breeding>>? = null

    var cancelBreedingListObserver: (Observer<List<Breeding>>)? = null
    var cancelBreedingList: LiveData<List<Breeding>>? = null

    override fun updateAll(userId: String) {
        // Go through every breeding and make sure alarm is set for the notification.
        val newObserver = Observer<List<Breeding>> {
            DefaultScheduler.execute {
                processBreedings(userId, it)
            }
        }

        breedingList = breedingRepository.getAllBreeding().apply {
            DefaultScheduler.postToMainThread { observeForever(newObserver) }
        }

        observerBreedingList = newObserver
    }

    @WorkerThread
    private fun processBreedings(userId: String, breedingList: List<Breeding>) {
        Timber.d("Setting all the alarms of breeding for user : $userId")

        val startWork = System.currentTimeMillis()

        breedingList.forEach { breeding ->
            breedingAlarmManager.setAlarmForBreeding(breeding)
        }

        Timber.d("Work finished of setting all the alarms of breeding in ${System.currentTimeMillis() - startWork} ms")
    }

    private fun clear() {
        observerBreedingList?.let {
            breedingList?.removeObserver(it)
        }
        cancelBreedingListObserver?.let {
            cancelBreedingList?.removeObserver(it)
        }
        observerBreedingList = null
        cancelBreedingListObserver = null
        breedingList = null
        cancelBreedingList = null
    }

    override fun cancelAll(onComplete: () -> Unit) {
        Timber.d("Cancelling all the breeding alarm")

        val newObserver = Observer<List<Breeding>> {
            DefaultScheduler.execute {
                cancelAllBreeding(it)
                Timber.d("Cancelled all the breeding alarm")
            }
            onComplete()
        }

        cancelBreedingList = breedingRepository.getAllBreeding().apply {
            DefaultScheduler.postToMainThread { observeForever(newObserver) }
        }

        cancelBreedingListObserver = newObserver

        clear()
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