package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.cattlenotes.shared.domain.cattle.detail.GetCattleUseCase
import com.pr656d.cattlenotes.shared.domain.result.Event
import com.pr656d.cattlenotes.shared.domain.result.Result
import com.pr656d.cattlenotes.shared.domain.result.Result.Error
import com.pr656d.cattlenotes.shared.domain.result.Result.Success
import javax.inject.Inject

class CattleDetailViewModel @Inject constructor(
    private val getCattleUseCase: GetCattleUseCase,
    private val deleteCattleUseCase: DeleteCattleUseCase
) : ViewModel() {

    private val cattleResult = MutableLiveData<Result<Cattle>>()

    private val _cattle = MediatorLiveData<Cattle>()
    val cattle: LiveData<Cattle>
        get() = _cattle

    fun fetchCattle(cattle: Cattle) {
        // Set cattle
        _cattle.value = cattle
        // Fetch cattle from database to update if there are any changes
        getCattleUseCase(cattle.id, cattleResult)
    }

    init {
        _cattle.addSource(cattleResult) { result ->
            when (result) {
                is Success -> {
                    val newCattle = result.data

                    // Update cattle if there are any changes
                    if (cattle.value != newCattle) {
                        _cattle.value = newCattle
                    }
                }
                is Error -> navigateUp()
            }
        }
    }

    private val _showMessage = MutableLiveData<@StringRes Int>()
    val showMessage: LiveData<Int>
        get() = _showMessage

    private val _launchAllBreeding = MutableLiveData<Event<Cattle>>()
    val launchAllBreeding: LiveData<Event<Cattle>>
        get() = _launchAllBreeding

    private val _launchAddBreeding = MutableLiveData<Event<Cattle>>()
    val launchAddBreeding: LiveData<Event<Cattle>>
        get() = _launchAddBreeding

    private val _launchActiveBreeding = MutableLiveData<Event<Cattle>>()
    val launchActiveBreeding: LiveData<Event<Cattle>>
        get() = _launchActiveBreeding

    private val _launchEditCattle = MutableLiveData<Event<Cattle>>()
    val launchEditCattle: LiveData<Event<Cattle>>
        get() = _launchEditCattle

    private val _launchDeleteConfirmation = MutableLiveData<Event<Unit>>()
    val launchDeleteConfirmation: LiveData<Event<Unit>>
        get() = _launchDeleteConfirmation

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    fun showAllBreeding() {
        cattle.value?.let {
            _launchAllBreeding.value = Event(it)
        }
    }

    fun addNewBreeding() {
        cattle.value?.let {
            _launchAddBreeding.value = Event(it)
        }
    }

    fun showActiveBreeding() {
        cattle.value?.let {
            _launchActiveBreeding.value = Event(it)
        }
    }

    fun editCattle() {
        cattle.value?.let {
            _launchEditCattle.value = Event(it)
        }
    }

    fun deleteCattle() {
        val result = deleteCattleUseCase(cattle.value!!).value
        when (result) {
            is Success -> navigateUp()
            is Error -> _showMessage.postValue(R.string.error_unknown)
        }
    }

    fun deleteCattleConfirmation() {
        _launchDeleteConfirmation.value = Event(Unit)
    }

    fun navigateUp() {
        _navigateUp.value = Event(Unit)
    }
}