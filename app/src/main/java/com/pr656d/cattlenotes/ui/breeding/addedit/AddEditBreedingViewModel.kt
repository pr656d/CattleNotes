package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInsemination
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.AddBreedingUseCase
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.utils.FirestoreUtil
import javax.inject.Inject

class AddEditBreedingViewModel @Inject constructor(
    breedingBehaviour: BreedingBehaviour,
    private val addBreedingUseCase: AddBreedingUseCase,
    private val updateBreedingUseCase: UpdateBreedingUseCase
) : ViewModel(), BreedingBehaviour by breedingBehaviour {

    private val _editing = MutableLiveData(false)

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

    private val _showBreedingCompletedDialogWithAddCattleOption = MutableLiveData<Event<Unit>>()
    val showBreedingCompletedDialogWithAddCattleOption: LiveData<Event<Unit>>
        get() = _showBreedingCompletedDialogWithAddCattleOption

    private var launchAddNewCattleWhenSaveCompleted = false

    private val _launchAddNewCattleScreen = MediatorLiveData<Event<Cattle>>()
    val launchAddNewCattleScreen: LiveData<Event<Cattle>>
        get() = _launchAddNewCattleScreen

    init {
        _saving.addSource(addUpdateBreedingResult) {
            _saving.value = false
        }

        _navigateUp.addSource(addUpdateBreedingResult) {
            if (!launchAddNewCattleWhenSaveCompleted)
                (it as? Success)?.let {
                    navigateUp()
                }
        }

        _launchAddNewCattleScreen.addSource(addUpdateBreedingResult) {
            if (launchAddNewCattleWhenSaveCompleted)
                (it as? Success)?.let {
                    _launchAddNewCattleScreen.postValue(Event(cattle.value!!))
                }
        }

        _showMessage.addSource(cattle) {
            if (it == null) _showMessage.postValue(Event(R.string.error_cattle_not_found))
        }

        _showMessage.addSource(addUpdateBreedingResult) {
            (it as? Error)?.let {
                _showMessage.value = Event(R.string.retry)
            }
        }
    }

    fun setBreeding(id: String) {
        _editing.value = true
        breedingId.postValue(id)
    }

    fun setCattle(id: String) = cattleId.postValue(id)

    fun save() = save(breedingCompletedConfirmation = false)

    fun save(breedingCompletedConfirmation: Boolean, saveAndAddNewCattle: Boolean = false) {
        val cattle = cattle.value ?: return

        if (aiDate.value != null) {
            val newBreedingCycle = getBreedingCycle(cattle)

            if (newBreedingCycle.breedingCompleted && !breedingCompletedConfirmation) {
                showBreedingCompletedScreen()
                return
            }

            if (saveAndAddNewCattle)
                launchAddNewCattleWhenSaveCompleted = true

            if (oldBreeding.value != newBreedingCycle) {
                _saving.value = true

                if (oldBreeding.value == null)
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

    private fun showBreedingCompletedScreen() {
        if (calvingStatus.value == true)
            _showBreedingCompletedDialogWithAddCattleOption.postValue(Event(Unit))
        else
            _showBreedingCompletedDialog.postValue(Event(Unit))
    }

    private fun getBreedingCycle(cattle: Cattle): Breeding =
        Breeding(
            id = oldBreeding.value?.id ?: FirestoreUtil.autoId(),
            cattleId = cattle.id,
            artificialInsemination = ArtificialInsemination(
                aiDate.value!!,
                didBy.value,
                bullName.value,
                strawCode.value
            ),
            repeatHeat = BreedingEvent.RepeatHeat(
                expectedOn = repeatHeatExpectedOn.value!!,
                status = repeatHeatStatus.value,
                doneOn = repeatHeatDoneOn.value
            ),
            pregnancyCheck = BreedingEvent.PregnancyCheck(
                expectedOn = pregnancyCheckExpectedOn.value!!,
                status = pregnancyCheckStatus.value,
                doneOn = pregnancyCheckDoneOn.value
            ),
            dryOff = BreedingEvent.DryOff(
                expectedOn = dryOffExpectedOn.value!!,
                status = dryOffStatus.value,
                doneOn = dryOffDoneOn.value
            ),
            calving = BreedingEvent.Calving(
                expectedOn = calvingExpectedOn.value!!,
                status = calvingStatus.value,
                doneOn = calvingDoneOn.value
            )
        )

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