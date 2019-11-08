package com.pr656d.cattlenotes.ui.cattle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.common.setValueIfNew
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import javax.inject.Inject

class CattleViewModel @Inject constructor(
    networkHelper: NetworkHelper,
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel(networkHelper) {

    private val _cattleList = MutableLiveData<List<Cattle>>()
    val cattleList: LiveData<List<Cattle>> = _cattleList

    override fun onCreate() {
        _cattleList.setValueIfNew(
            arrayListOf<Cattle>().apply {
                for (i in 0..10)
                    add(Cattle(tagNumber = "123456789001", name = "Janki",
                        breed = Cattle.CattleBreed.KANKREJ,
                        group = Cattle.CattleGroup.MILKING,
                        type = Cattle.CattleType.COW, calving = 3))
            }
        )
    }
}