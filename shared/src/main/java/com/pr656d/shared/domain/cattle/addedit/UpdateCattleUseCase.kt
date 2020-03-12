package com.pr656d.shared.domain.cattle.addedit

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class UpdateCattleUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<Cattle, Unit>() {
    override fun execute(parameters: Cattle) {
        cattleRepository.updateCattle(parameters)
    }
}