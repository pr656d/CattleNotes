package com.pr656d.cattlenotes.ui.cattle.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.domain.result.Event
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : ViewModel(), CattleActionListener {

    val list = cattleDataRepository.getAllCattle()

    private val _cattleList = MediatorLiveData<List<Cattle>>()
    val cattleList: LiveData<List<Cattle>> = _cattleList

    private val _launchAddCattleScreen = MutableLiveData<Event<Unit>>()
    val launchAddCattleScreen: LiveData<Event<Unit>> = _launchAddCattleScreen

    private val _launchCattleDetail = MutableLiveData<Event<Cattle>>()
    val launchCattleDetail: LiveData<Event<Cattle>> = _launchCattleDetail

    private val _isEmpty = MediatorLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        _cattleList.addSource(list) {
            _cattleList.value = it
        }
        _isEmpty.addSource(list) {
            _isEmpty.value = it.isNullOrEmpty()
        }
    }

    fun addCattle() { _launchAddCattleScreen.value = Event(Unit) }

    override fun openCattle(cattle: Cattle) {
        _launchCattleDetail.value = Event(cattle)
    }
}