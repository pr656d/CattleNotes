package com.pr656d.shared.domain.cattle.list

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import javax.inject.Inject

open class LoadCattleListUseCase @Inject constructor(
    private val cattleRepository: CattleRepository
) {
    operator fun invoke(): LiveData<List<Cattle>> {
        return cattleRepository.getAllCattle()
    }
}