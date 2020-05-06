package com.pr656d.shared.domain.milk

import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class LoadAllNewMilkFromSmsUseCase @Inject constructor(
    private val milkRepository: MilkRepository
) : UseCase<Milk.Source.Sms, List<Milk>>() {
    override fun execute(parameters: Milk.Source.Sms): List<Milk> {
        val dbMilkList = milkRepository.getAllMilkUnobserved()
        val smsMilkList = milkRepository.getAllMilkFromSms(parameters)
        /**
         * Remove elements exist in db from [smsMilkList].
         */
        return smsMilkList.minus(dbMilkList)
    }
}