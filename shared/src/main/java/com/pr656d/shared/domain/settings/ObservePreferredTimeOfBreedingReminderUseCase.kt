package com.pr656d.shared.domain.settings

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalTime
import javax.inject.Inject

class ObservePreferredTimeOfBreedingReminderUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : MediatorUseCase<Unit, LocalTime>() {
    override fun execute(parameters: Unit) {
        result.addSource(preferenceStorage.observePreferredTimeOfBreedingReminder) {
            result.postValue(Result.Success(TimeUtils.toLocalTime(it)))
        }
    }
}