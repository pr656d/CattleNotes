package com.pr656d.shared.domain.settings

import androidx.lifecycle.LiveData
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import org.threeten.bp.LocalTime
import javax.inject.Inject

class ObservePreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) {
    operator fun invoke(): LiveData<LocalTime> {
        return preferenceStorageRepository.getObservablePreferredTimeOfBreedingReminder()
    }
}