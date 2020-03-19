package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.model.BreedingCycle
import com.pr656d.model.BreedingCycle.ArtificialInseminationInfo
import com.pr656d.model.BreedingCycle.BreedingEvent
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.AddBreedingUseCase
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class AddEditBreedingViewModel @Inject constructor(
    breedingBehaviour: BreedingBehaviour,
    private val addBreedingUseCase: AddBreedingUseCase,
    private val updateBreedingUseCase: UpdateBreedingUseCase,
    private val getCattleByIdUseCase: GetCattleByIdUseCase
) : ViewModel(),
    BreedingBehaviour by breedingBehaviour {

    private var oldBreedingCycle: BreedingCycle? = null

    private val _editing = MutableLiveData<Boolean>(false)

    val editing: LiveData<Boolean>
        get() = _editing

    private val addUpdateBreedingResult = MutableLiveData<Result<Unit>>()

    private val getCattleResult = MutableLiveData<Result<Cattle>>()

    private val _saving = MediatorLiveData<Boolean>().apply { value = false }
    val saving: LiveData<Boolean> = _saving

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val _showBackConfirmationDialog = MutableLiveData<Event<Unit>>()
    val showBackConfirmationDialog: LiveData<Event<Unit>>
        get() = _showBackConfirmationDialog

    private val _navigateUp = MediatorLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    init {
        cattle.addSource(getCattleResult) { result ->
            (result as? Success)?.data?.let {
                cattle.value = it
            }
        }

        _saving.addSource(addUpdateBreedingResult) {
            _saving.value = false
        }

        _navigateUp.addSource(addUpdateBreedingResult) {
            (it as? Success)?.let {
                navigateUp()
            }
        }

        _showMessage.addSource(addUpdateBreedingResult) {
            (it as? Error)?.let {
                _showMessage.value = Event(R.string.retry)
            }
        }
    }

    fun setBreedingCycle(breedingCycle: BreedingCycle) {
        _editing.value = true
        oldBreedingCycle = breedingCycle
        getCattleByIdUseCase(breedingCycle.cattleId, getCattleResult)
        breedingCycle.bindData()
    }

    fun save(cattle: Cattle) {
        if (aiDate.value != null) {
            val newBreedingCycle = getBreedingCycle(cattle)

            if (oldBreedingCycle != newBreedingCycle) {
                _saving.value = true

                if (oldBreedingCycle == null)
                    addBreedingUseCase(newBreedingCycle, addUpdateBreedingResult)
                else
                    updateBreedingUseCase(newBreedingCycle, addUpdateBreedingResult)

            } else {
                navigateUp()
            }
        } else {
            _showMessage.value = Event(R.string.provide_ai_date)
        }
    }

    private fun getBreedingCycle(cattle: Cattle): BreedingCycle =
        BreedingCycle(
            cattle.id,
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

    private fun BreedingCycle.bindData() {
        // AI
        aiDate.value = artificialInsemination?.date
        didBy.value = artificialInsemination?.didBy
        bullName.value = artificialInsemination?.bullName
        strawCode.value = artificialInsemination?.strawCode
        // Repeat Heat
        repeatHeatStatus.value = repeatHeat?.status
        repeatHeatDoneOn.value = repeatHeat?.doneOn
        // Pregnancy Check
        pregnancyCheckStatus.value = pregnancyCheck?.status
        pregnancyCheckDoneOn.value = pregnancyCheck?.doneOn
        // Dry Off
        dryOffStatus.value = dryOff?.status
        dryOffDoneOn.value = dryOff?.doneOn
        // Calving
        calvingStatus.value = calving?.status
        calvingDoneOn.value = calving?.doneOn
    }

    fun onBackPressed(backConfirmation: Boolean = false) {
        if (aiDate.value == null) {
            navigateUp()
            return
        }
        if (backConfirmation)
            navigateUp()
        else
            showBackConfirmation()
    }

    private fun showBackConfirmation() {
        _showBackConfirmationDialog.value = Event(Unit)
    }

    private fun navigateUp() {
        _navigateUp.value = Event(Unit)
    }
}