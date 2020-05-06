package com.pr656d.shared.domain.milk

import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class AddMilkUseCase @Inject constructor(
    private val milkRepository: MilkRepository
) : UseCase<Milk, Unit>() {
    override fun execute(parameters: Milk) {
        milkRepository.addMilk(parameters)
    }
}