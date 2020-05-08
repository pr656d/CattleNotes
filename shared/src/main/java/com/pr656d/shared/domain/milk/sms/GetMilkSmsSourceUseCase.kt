package com.pr656d.shared.domain.milk.sms

import com.pr656d.model.Milk
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.utils.toMilkSmsSource
import javax.inject.Inject

open class GetMilkSmsSourceUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UseCase<Unit, Milk.Source.Sms?>() {
    override fun execute(parameters: Unit): Milk.Source.Sms? {
        return preferenceStorage.selectedMilkSmsSource?.toMilkSmsSource()
    }
}