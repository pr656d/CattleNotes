package com.pr656d.shared.domain.milk

import androidx.lifecycle.LiveData
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import javax.inject.Inject

open class LoadMilkListUseCase @Inject constructor(
    private val milkRepository: MilkRepository
) {
    operator fun invoke(): LiveData<List<Milk>> {
        return milkRepository.getAllMilk()
    }
}