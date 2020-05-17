package com.pr656d.shared.domain.milk.sms

import com.pr656d.model.Milk
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class GetMilkSmsSourceUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) : UseCase<Unit, Milk.Source.Sms?>() {
    override fun execute(parameters: Unit): Milk.Source.Sms? {
        return preferenceStorageRepository.getPreferredMilkSmsSource()
    }
}