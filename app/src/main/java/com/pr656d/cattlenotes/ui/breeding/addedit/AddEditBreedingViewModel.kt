package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.utils.BreedingUtil
import com.pr656d.model.BreedingCycle
import com.pr656d.model.BreedingCycle.ArtificialInseminationInfo
import com.pr656d.model.BreedingCycle.BreedingEvent
import com.pr656d.model.Cattle
import com.pr656d.shared.data.breeding.BreedingDataRepository
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AddEditBreedingViewModel @Inject constructor(
    private val breedingDataRepository: BreedingDataRepository
) : ViewModel() {

    val active = MutableLiveData<Boolean>(false)

    val aiDate = MutableLiveData<LocalDate>()

    val didBy = MutableLiveData<String>()

    val bullName = MutableLiveData<String>()

    val strawCode = MutableLiveData<String>()

    val repeatHeatExpectedOn: LiveData<LocalDate> = aiDate.map {
        BreedingUtil.getExpectedRepeatHeatDate(it)
    }

    val repeatHeatStatus = MutableLiveData<Boolean?>()

    val repeatHeatDoneOn = MutableLiveData<LocalDate>()

    val pregnancyCheckExpectedOn: LiveData<LocalDate> = aiDate.map {
        BreedingUtil.getExpectedPregnancyCheckDate(it)
    }

    val pregnancyCheckStatus = MutableLiveData<Boolean?>()

    val pregnancyCheckDoneOn = MutableLiveData<LocalDate>()

    val dryOffExpectedOn : LiveData<LocalDate> = aiDate.map {
        BreedingUtil.getExpectedDryOffDate(it)
    }

    val dryOffStatus = MutableLiveData<Boolean?>()

    val dryOffDoneOn = MutableLiveData<LocalDate>()

    val calvingExpectedOn: LiveData<LocalDate> = aiDate.map {
        BreedingUtil.getExpectedCalvingDate(it)
    }

    val calvingStatus = MutableLiveData<Boolean?>()

    val calvingDoneOn = MutableLiveData<LocalDate>()

    private val _saving = MutableLiveData<Boolean>(false)
    val saving: LiveData<Boolean> = _saving

    private val _showMessage = MutableLiveData<@StringRes Int>()
    val showMessage: LiveData<Event<Int>> = _showMessage.map { Event(it) }

    private val _showBackConfirmationDialog = MutableLiveData<Event<Unit>>()
    val showBackConfirmationDialog: LiveData<Event<Unit>>
        get() = _showBackConfirmationDialog

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    fun save(cattle: Cattle) {
        val toggleSaving: suspend () -> Unit = {
            withContext(Dispatchers.Main) {
                _saving.value = _saving.value!!.not()
            }
        }

        if (aiDate.value != null) {
            val breedingCycle = getBreedingCycle(cattle)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    toggleSaving()
                    breedingDataRepository.addBreeding(breedingCycle)
                    toggleSaving()
                    withContext(Dispatchers.Main) {
                        navigateUp()
                    }
                } catch (e: Exception) {
                    Logger.d(AddEditBreedingFragment.TAG, "${e.printStackTrace()}")
                    if (_saving.value!!)
                        toggleSaving()
                    _showMessage.postValue(R.string.retry)
                }
            }
        } else {
            _showMessage.value = R.string.provide_ai_date
        }
    }

    private fun getBreedingCycle(cattle: Cattle): BreedingCycle =
        BreedingCycle(
            cattle.id,
            cattle.tagNumber,
            cattle.type,
            active.value ?: false,
            aiDate.value?.let {
                ArtificialInseminationInfo(
                    it,
                    didBy.value,
                    bullName.value,
                    strawCode.value
                )
            },
            repeatHeatExpectedOn.value?.let {
                BreedingEvent(it, repeatHeatStatus.value, repeatHeatDoneOn.value)
            },
            pregnancyCheckExpectedOn.value?.let {
                BreedingEvent(it, pregnancyCheckStatus.value, pregnancyCheckDoneOn.value)
            },
            dryOffExpectedOn.value?.let {
                BreedingEvent(it, dryOffStatus.value, dryOffDoneOn.value)
            },
            calvingExpectedOn.value?.let {
                BreedingEvent(it, calvingStatus.value, calvingDoneOn.value)
            }
        )

    fun onBackPressed() {
        _showBackConfirmationDialog.value = Event(Unit)
    }

    fun navigateUp() {
        _navigateUp.value = Event(Unit)
    }
}