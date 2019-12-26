package com.pr656d.cattlenotes.ui.main.cattle.list

import androidx.lifecycle.LiveData
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    val cattleList: LiveData<List<Cattle>> by lazy { cattleDataRepository.getAllCattle() }

}