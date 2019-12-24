package com.pr656d.cattlenotes.ui.main.cattle.list

import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

//    val cattleList by lazy { cattleDataRepository.getAllCattle() }

}