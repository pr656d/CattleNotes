package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.utils.BreedingUtil
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
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AddEditBreedingViewModel @Inject constructor(
    private val addBreedingUseCase: AddBreedingUseCase,
    private val updateBreedingUseCase: UpdateBreedingUseCase,
    private val getCattleByIdUseCase: GetCattleByIdUseCase
) : ViewModel() {

    private var oldBreedingCycle: BreedingCycle? = null

    private val _editing = MutableLiveData<Boolean>(false)

    val editing: LiveData<Boolean>
        get() = _editing

    private val addUpdateBreedingResult = MutableLiveData<Result<Unit>>()

    private val getCattleResult = MutableLiveData<Result<Cattle>>()

    val cattle = MediatorLiveData<Cattle>()

    val active = MutableLiveData<Boolean>(false)

    val aiDate = MutableLiveData<LocalDate?>(null)

    val hasAiDate: LiveData<Boolean>
        get() = aiDate.map { it?.let { true } ?: false }

    val didBy = MutableLiveData<String>()

    val bullName = MutableLiveData<String>()

    val strawCode = MutableLiveData<String>()

    val repeatHeatExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedRepeatHeatDate(it) }
    }

    val repeatHeatStatus = MediatorLiveData<Boolean?>()

    val repeatHeatDoneOn = MutableLiveData<LocalDate>()

    val pregnancyCheckExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedPregnancyCheckDate(it) }
    }

    val pregnancyCheckStatus = MediatorLiveData<Boolean?>()

    val pregnancyCheckDoneOn = MutableLiveData<LocalDate>()

    val dryOffExpectedOn : LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedDryOffDate(it) }
    }

    val dryOffStatus = MediatorLiveData<Boolean?>()

    val dryOffDoneOn = MutableLiveData<LocalDate>()

    val calvingExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedCalvingDate(it) }
    }

    val calvingStatus = MediatorLiveData<Boolean?>()

    val calvingDoneOn = MutableLiveData<LocalDate>()

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

        repeatHeatStatus.addSource(aiDate) {
            if (it == null) repeatHeatStatus.value = null
        }

        pregnancyCheckStatus.addSource(repeatHeatStatus) {
            if (it == null) pregnancyCheckStatus.value = null
        }

        dryOffStatus.addSource(pregnancyCheckStatus) {
            if (it == null) dryOffStatus.value = null
        }

        calvingStatus.addSource(dryOffStatus) {
            if (it == null) calvingStatus.value = null
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