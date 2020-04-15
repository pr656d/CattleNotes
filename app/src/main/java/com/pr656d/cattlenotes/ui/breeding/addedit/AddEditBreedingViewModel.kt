package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInseminationInfo
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.Breeding.BreedingEvent.Type.*
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

    private var oldBreeding: Breeding? = null

    private val _editing = MutableLiveData<Boolean>(false)

    val editing: LiveData<Boolean>
        get() = _editing

    private val addUpdateBreedingResult = MutableLiveData<Result<Unit>>()

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

    private val _showBreedingCompletedDialog = MutableLiveData<Event<Unit>>()
    val showBreedingCompletedDialog: LiveData<Event<Unit>>
        get() = _showBreedingCompletedDialog

    init {
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

    fun setBreeding(breeding: Breeding) {
        _editing.value = true
        // Trigger cattle to be fetched and get Live updates
        fetchCattle(breeding.cattleId)
        breeding.bindData()
        oldBreeding = breeding
    }

    fun setCattle(c: Cattle) {
        cattle.value = c
        // Trigger cattle to be fetched and get Live updates
        fetchCattle(c.id)
    }

    fun save() = save(breedingCompletedConfirmation = false)

    fun save(breedingCompletedConfirmation: Boolean) {
        val cattle = cattle.value ?: return

        if (aiDate.value != null) {
            val newBreedingCycle = getBreedingCycle(cattle)

            if (newBreedingCycle.breedingCompleted && !breedingCompletedConfirmation) {
                _showBreedingCompletedDialog.postValue(Event(Unit))
                return
            }

            if (oldBreeding != newBreedingCycle) {
                _saving.value = true

                if (oldBreeding == null)
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

    private fun fetchCattle(cattleId: String) {
        cattle.addSource(getCattleByIdUseCase(cattleId)) { newCattle ->
            if (cattle.value != newCattle)
                cattle.value = newCattle
        }
    }

    private fun getBreedingCycle(cattle: Cattle): Breeding =
        Breeding(
            cattle.id,
            ArtificialInseminationInfo(
                aiDate.value!!,
                didBy.value,
                bullName.value,
                strawCode.value
            ),
            BreedingEvent(
                type = REPEAT_HEAT,
                expectedOn = repeatHeatExpectedOn.value!!,
                status = repeatHeatStatus.value,
                doneOn = repeatHeatDoneOn.value
            ),
            BreedingEvent(
                type = PREGNANCY_CHECK,
                expectedOn = pregnancyCheckExpectedOn.value!!,
                status = pregnancyCheckStatus.value,
                doneOn = pregnancyCheckDoneOn.value
            ),
            BreedingEvent(
                type = DRY_OFF,
                expectedOn = dryOffExpectedOn.value!!,
                status = dryOffStatus.value,
                doneOn = dryOffDoneOn.value
            ),
            BreedingEvent(
                expectedOn = calvingExpectedOn.value!!,
                status = calvingStatus.value,
                doneOn = calvingDoneOn.value,
                type = CALVING
            )
        ).apply {
            oldBreeding?.id?.let { id = it }
        }

    private fun Breeding.bindData() {
        // AI
        aiDate.value = artificialInsemination.date
        didBy.value = artificialInsemination.didBy
        bullName.value = artificialInsemination.bullName
        strawCode.value = artificialInsemination.strawCode
        // Repeat Heat
        repeatHeatStatus.value = repeatHeat.status
        repeatHeatDoneOn.value = repeatHeat.doneOn
        // Pregnancy Check
        pregnancyCheckStatus.value = pregnancyCheck.status
        pregnancyCheckDoneOn.value = pregnancyCheck.doneOn
        // Dry Off
        dryOffStatus.value = dryOff.status
        dryOffDoneOn.value = dryOff.doneOn
        // Calving
        calvingStatus.value = calving.status
        calvingDoneOn.value = calving.doneOn
    }

    fun onBackPressed(backConfirmation: Boolean = false) {
        if (aiDate.value == null && !_editing.value!!) {
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