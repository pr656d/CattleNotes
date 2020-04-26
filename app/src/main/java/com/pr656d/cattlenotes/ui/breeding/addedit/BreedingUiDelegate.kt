package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.lifecycle.*
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.utils.BreedingUtil
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * Holds UI data for [AddEditBreedingFragment].
 */
interface BreedingUiDelegate {

    val cattleId: MutableLiveData<String>

    /* Cattle */
    val cattle: LiveData<Cattle?>

    val active: MutableLiveData<Boolean>

    /* Artificial Insemination */

    val aiDate: MutableLiveData<LocalDate?>

    val didBy: MutableLiveData<String>

    val bullName: MutableLiveData<String>

    val strawCode: MutableLiveData<String>

    /* Repeat Heat */

    val repeatHeatExpectedOn: LiveData<LocalDate?>

    val repeatHeatStatus: MediatorLiveData<Boolean?>

    val repeatHeatDoneOn: MutableLiveData<LocalDate>

    /* Pregnancy Check */

    val pregnancyCheckExpectedOn: LiveData<LocalDate?>

    val pregnancyCheckStatus: MediatorLiveData<Boolean?>

    val pregnancyCheckDoneOn: MutableLiveData<LocalDate>

    /* Dry Off */

    val dryOffExpectedOn : LiveData<LocalDate?>

    val dryOffStatus: MediatorLiveData<Boolean?>

    val dryOffDoneOn: MutableLiveData<LocalDate>

    /* Calving */

    val calvingExpectedOn: LiveData<LocalDate?>

    val calvingStatus: MediatorLiveData<Boolean?>

    val calvingDoneOn: MutableLiveData<LocalDate>
}

class BreedingUiImplDelegate @Inject constructor(
    getCattleByIdUseCase: GetCattleByIdUseCase
) : BreedingUiDelegate {

    override val cattleId: MutableLiveData<String> = MutableLiveData()

    /* Cattle */
    override val cattle = cattleId.switchMap { getCattleByIdUseCase(it) }

    override val active: MutableLiveData<Boolean> = MutableLiveData(false)

    /* Artificial Insemination */

    override val aiDate: MutableLiveData<LocalDate?> = MutableLiveData(null)

    override val didBy: MutableLiveData<String> = MutableLiveData()

    override val bullName = MutableLiveData<String>()

    override val strawCode = MutableLiveData<String>()

    /* Dry Off */

    override val repeatHeatExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedRepeatHeatDate(it) }
    }

    override val repeatHeatStatus: MediatorLiveData<Boolean?> = MediatorLiveData()

    override val repeatHeatDoneOn: MutableLiveData<LocalDate> = MutableLiveData()

    /* Pregnancy Check */

    override val pregnancyCheckExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedPregnancyCheckDate(it) }
    }

    override val pregnancyCheckStatus = MediatorLiveData<Boolean?>()

    override val pregnancyCheckDoneOn = MutableLiveData<LocalDate>()

    /* Dry Off */

    override val dryOffExpectedOn : LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedDryOffDate(it) }
    }

    override val dryOffStatus: MediatorLiveData<Boolean?> = MediatorLiveData()

    override val dryOffDoneOn: MutableLiveData<LocalDate> = MutableLiveData()

    /* Calving */

    override val calvingExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedCalvingDate(it) }
    }

    override val calvingStatus: MediatorLiveData<Boolean?> = MediatorLiveData()

    override val calvingDoneOn: MutableLiveData<LocalDate> = MutableLiveData()
}