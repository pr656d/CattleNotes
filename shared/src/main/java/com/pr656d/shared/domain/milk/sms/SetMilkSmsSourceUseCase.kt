package com.pr656d.shared.domain.milk.sms

import com.pr656d.model.Milk
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class SetMilkSmsSourceUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Milk.Source.Sms, Unit>() {
    override fun execute(parameters: Milk.Source.Sms) {
        preferenceStorageRepository.setPreferredMilkSmsSource(parameters)
    }
}