package com.pr656d.cattlenotes.shared.domain.cattle.addedit.parent

import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository
import com.pr656d.cattlenotes.shared.domain.UseCase
import javax.inject.Inject

class GetParentListUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) : UseCase<Long, List<Cattle>>() {
    override fun execute(parameters: Long): List<Cattle> {
        return cattleRepository.getAllCattle().filter { it.id != parameters }
    }
}