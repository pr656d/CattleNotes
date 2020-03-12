package com.pr656d.shared.domain.cattle.addedit.parent

import com.pr656d.model.Cattle
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.cattle.list.LoadObservableCattleListUseCase
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class GetParentListUseCase @Inject constructor(
    private val loadObservableCattleListUseCase: LoadObservableCattleListUseCase
) : MediatorUseCase<Long, List<Cattle>>() {
    override fun execute(parameters: Long) {
        result.addSource(loadObservableCattleListUseCase()) { list ->
            val filteredList = list.filter {
                it.tagNumber != parameters
            }
            result.postValue(Result.Success(filteredList))
        }
    }
}