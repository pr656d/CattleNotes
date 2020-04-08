package com.pr656d.shared.domain.cattle.detail

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class GetCattleByTagNumberUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<String, Cattle>() {

    override fun execute(parameters: String): Cattle {
        return cattleRepository.getCattleByTagNumber(parameters)
            ?: throw Exception("Cattle not found with tag number $parameters")
    }
}