package com.pr656d.cattlenotes.ui.main.cattle.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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

    fun fetchCattle(c: Cattle) {
        cattle.value = c

        viewModelScope.launch(Dispatchers.IO) {
            val newCattle = cattleDataRepository.getCattle(c.id)
            withContext(Dispatchers.Main) {
                newCattle?.let {
                    if (newCattle != cattle.value)
                        cattle.value = it
                }
            }
        }
    }

    fun showAllBreeding() {
        cattle.value!!.tagNumber.toString().let {
            _action.value = Event(Destination(ALL_BREEDING_SCREEN, it))
        }
    }

    fun addNewBreeding() {
        cattle.value!!.tagNumber.toString().let {
            _action.value = Event(Destination(ADD_BREEDING_SCREEN, it))
        }
    }

    fun showActiveBreeding() {
        cattle.value!!.tagNumber.toString().let {
            _action.value = Event(Destination(ACTIVE_BREEDING, it))
        }
    }

    fun editCattle() {
        cattle.value?.let {
            _action.value = Event(Destination(EDIT_CATTLE_SCREEN, Gson().toJson(it)))
        }
    }

    fun deleteCattle() {
        viewModelScope.launch {
            cattleDataRepository.deleteCattle(cattle.value!!)
            navigateUp()
        }
    }

    fun deleteCattleConfirmation() {
        _action.value = Event(Destination(DELETE_CATTLE_CONFIRMATION))
    }

    fun navigateUp() {
        _action.value = Event(Destination(NAVIGATE_UP))
    }

    data class Destination(val destination: DESTINATIONS, val data: String? = null) {
        enum class DESTINATIONS {
            ACTIVE_BREEDING, ALL_BREEDING_SCREEN, ADD_BREEDING_SCREEN, EDIT_CATTLE_SCREEN,
            DELETE_CATTLE_CONFIRMATION, NAVIGATE_UP
        }
    }
}