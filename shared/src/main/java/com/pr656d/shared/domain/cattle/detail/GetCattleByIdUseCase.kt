package com.pr656d.shared.domain.cattle.detail

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import javax.inject.Inject

open class GetCattleByIdUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) {
    operator fun invoke(cattleId: String): LiveData<Cattle?> {
        return cattleRepository.getCattleById(cattleId)
    }
}