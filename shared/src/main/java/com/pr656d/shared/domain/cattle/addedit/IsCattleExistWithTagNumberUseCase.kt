package com.pr656d.shared.domain.cattle.addedit

import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

class IsCattleExistWithTagNumberUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<String, Boolean>() {
    override fun execute(parameters: String): Boolean {
        return cattleRepository.getCattleByTagNumber(parameters) != null
    }
}