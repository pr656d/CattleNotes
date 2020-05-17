package com.pr656d.shared.domain.milk.sms

import androidx.lifecycle.LiveData
import com.pr656d.model.Milk
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import javax.inject.Inject

open class ObserveMilkSmsSourceUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) {
    operator fun invoke(): LiveData<Milk.Source.Sms> {
        return preferenceStorageRepository.getObservablePreferredMilkSmsSource()
    }
}