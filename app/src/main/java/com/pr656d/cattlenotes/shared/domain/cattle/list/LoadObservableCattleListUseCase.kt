package com.pr656d.cattlenotes.shared.domain.cattle.list

import androidx.lifecycle.LiveData
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository
import javax.inject.Inject

open class LoadObservableCattleListUseCase @Inject constructor(
    private val repository: CattleRepository
) {
    operator fun invoke(): LiveData<List<Cattle>> {
        return repository.getObservableAllCattle()
    }
}