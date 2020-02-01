package com.pr656d.cattlenotes.ui.main.cattle.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailViewModel.Destination.DESTINATIONS.*
import com.pr656d.cattlenotes.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CattleDetailViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : ViewModel() {

    val cattle = MutableLiveData<Cattle>()

    private val _action = MutableLiveData<Event<Destination>>()
    val action: LiveData<Event<Destination>> = _action

    fun fetchCattle(tagNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val c = cattleDataRepository.getCattle(tagNumber.toLong())
            withContext(Dispatchers.Main) {
                c?.let { cattle.value = c }
            }
        }
    }

    fun showAllBreeding() {
        _action.value = Event(Destination(ALL_BREEDING_SCREEN))
    }

    fun addNewBreeding() {
        _action.value = Event(Destination(ADD_BREEDING_SCREEN))
    }

    fun showActiveBreeding() {
        _action.value = Event(Destination(ACTIVE_BREEDING))
    }

    fun editCattle() {
        _action.value = Event(Destination(EDIT_CATTLE_SCREEN, cattle.value!!.tagNumber.toString()))
    }

    data class Destination(val destination: DESTINATIONS, val data: Any = Unit) {
        enum class DESTINATIONS {
            ACTIVE_BREEDING, ALL_BREEDING_SCREEN, ADD_BREEDING_SCREEN, EDIT_CATTLE_SCREEN
        }
    }
}