package com.pr656d.cattlenotes.shared.domain.cattle.detail

import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class GetCattleUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<Long, Cattle>() {

    override fun execute(parameters: Long): Cattle {
        return cattleRepository.getCattleById(parameters)
    }
}