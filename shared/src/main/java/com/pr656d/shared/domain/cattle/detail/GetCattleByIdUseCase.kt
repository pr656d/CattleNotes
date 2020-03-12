package com.pr656d.shared.domain.cattle.detail

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class GetCattleByIdUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<Long, Cattle>() {

    override fun execute(parameters: Long): Cattle {
        return cattleRepository.getCattleById(parameters)
            ?: throw Exception("Cattle not found with id $parameters")
    }
}