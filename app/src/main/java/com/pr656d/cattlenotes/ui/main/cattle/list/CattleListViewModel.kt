package com.pr656d.cattlenotes.ui.main.cattle.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.utils.Event
import javax.inject.Inject

class CattleListViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : ViewModel() {

    val cattleList: LiveData<List<Cattle>> = cattleDataRepository.getAllCattle()

    private val _launchAddCattleScreen = MutableLiveData<Event<Unit>>()
    val launchAddCattleScreen: LiveData<Event<Unit>> = _launchAddCattleScreen

    fun addCattle() { _launchAddCattleScreen.value = Event(Unit) }

}