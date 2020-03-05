package com.pr656d.cattlenotes.shared.domain.cattle.addedit

import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class AddCattleUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<Cattle, Unit>() {
    override fun execute(parameters: Cattle) {
        cattleRepository.addCattle(parameters)
    }
}