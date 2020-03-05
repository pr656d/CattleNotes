package com.pr656d.cattlenotes.shared.domain.cattle.addedit

import com.pr656d.cattlenotes.data.repository.CattleRepository
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class IsCattleExistWithTagNumberUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<Long, Boolean>() {
    override fun execute(parameters: Long): Boolean {
        return cattleRepository.getCattleByTagNumber(parameters) != null
    }
}