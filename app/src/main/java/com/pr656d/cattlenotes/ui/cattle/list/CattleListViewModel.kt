package com.pr656d.cattlenotes.ui.cattle.list

import androidx.lifecycle.*
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.shared.domain.cattle.list.LoadObservableCattleListUseCase
import com.pr656d.cattlenotes.shared.domain.result.Event
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    loadObservableCattleListUseCase: LoadObservableCattleListUseCase
) : ViewModel(), CattleActionListener {

    val cattleList: LiveData<List<Cattle>> = loadObservableCattleListUseCase()

    private val _launchAddCattleScreen = MutableLiveData<Event<Unit>>()
    val launchAddCattleScreen: LiveData<Event<Unit>> = _launchAddCattleScreen

    private val _launchCattleDetail = MutableLiveData<Event<Cattle>>()
    val launchCattleDetail: LiveData<Event<Cattle>> = _launchCattleDetail

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>

    init {
        isEmpty = cattleList.map { it.isNullOrEmpty() }

        _loading.addSource(cattleList) {
            _loading.value = false
        }
    }

    fun addCattle() {
        _launchAddCattleScreen.value = Event(Unit)
    }

    override fun openCattle(cattle: Cattle) {
        _launchCattleDetail.value = Event(cattle)
    }
}