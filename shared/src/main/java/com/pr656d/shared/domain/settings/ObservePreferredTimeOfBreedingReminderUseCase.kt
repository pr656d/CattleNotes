package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import org.threeten.bp.LocalTime
import javax.inject.Inject

class ObservePreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : MediatorUseCase<Unit, LocalTime>() {
    override fun execute(parameters: Unit) {
        result.addSource(preferenceStorageRepository.getObservablePreferredTimeOfBreedingReminder()) {
            result.postValue(Result.Success(it))
        }
    }
}