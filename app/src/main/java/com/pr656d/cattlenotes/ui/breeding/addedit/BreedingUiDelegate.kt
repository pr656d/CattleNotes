/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.lifecycle.*
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.detail.GetBreedingByIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.utils.BreedingUtil
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * Holds UI data for [AddEditBreedingFragment].
 */
interface BreedingUiDelegate {

    val cattleId: MutableLiveData<String>

    val breedingId: MutableLiveData<String>

    /* Cattle */
    val cattle: LiveData<Cattle?>

    /* Breeding */
    val oldBreeding: LiveData<Breeding?>

    val active: MutableLiveData<Boolean>

    /* Artificial Insemination */

    val aiDate: MediatorLiveData<LocalDate?>

    val didBy: MediatorLiveData<String>

    val bullName: MediatorLiveData<String>

    val strawCode: MediatorLiveData<String>

    /* Repeat Heat */

    val repeatHeatExpectedOn: LiveData<LocalDate?>

    val repeatHeatStatus: MediatorLiveData<Boolean?>

    val repeatHeatDoneOn: MediatorLiveData<LocalDate>

    /* Pregnancy Check */

    val pregnancyCheckExpectedOn: LiveData<LocalDate?>

    val pregnancyCheckStatus: MediatorLiveData<Boolean?>

    val pregnancyCheckDoneOn: MediatorLiveData<LocalDate>

    /* Dry Off */

    val dryOffExpectedOn : LiveData<LocalDate?>

    val dryOffStatus: MediatorLiveData<Boolean?>

    val dryOffDoneOn: MediatorLiveData<LocalDate>

    /* Calving */

    val calvingExpectedOn: LiveData<LocalDate?>

    val calvingStatus: MediatorLiveData<Boolean?>

    val calvingDoneOn: MediatorLiveData<LocalDate>
}

class BreedingUiImplDelegate @Inject constructor(
    getCattleByIdUseCase: GetCattleByIdUseCase,
    getBreedingByIdUseCase: GetBreedingByIdUseCase
) : BreedingUiDelegate {

    override val cattleId: MutableLiveData<String> = MutableLiveData()

    override val breedingId: MutableLiveData<String> = MutableLiveData()

    /* Cattle */
    override val cattle = cattleId.switchMap { getCattleByIdUseCase(it) }

    override val oldBreeding: LiveData<Breeding?> = breedingId.switchMap { getBreedingByIdUseCase(it) }

    override val active: MutableLiveData<Boolean> = MutableLiveData(false)

    /* Artificial Insemination */

    override val aiDate = MediatorLiveData<LocalDate?>()

    override val didBy = MediatorLiveData<String>()

    override val bullName = MediatorLiveData<String>()

    override val strawCode = MediatorLiveData<String>()

    /* Repeat heat */

    override val repeatHeatExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedRepeatHeatDate(it) }
    }

    override val repeatHeatStatus: MediatorLiveData<Boolean?> = MediatorLiveData()

    override val repeatHeatDoneOn = MediatorLiveData<LocalDate>()

    /* Pregnancy Check */

    override val pregnancyCheckExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedPregnancyCheckDate(it) }
    }

    override val pregnancyCheckStatus = MediatorLiveData<Boolean?>()

    override val pregnancyCheckDoneOn = MediatorLiveData<LocalDate>()

    /* Dry Off */

    override val dryOffExpectedOn : LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedDryOffDate(it) }
    }

    override val dryOffStatus = MediatorLiveData<Boolean?>()

    override val dryOffDoneOn = MediatorLiveData<LocalDate>()

    /* Calving */

    override val calvingExpectedOn: LiveData<LocalDate?> = aiDate.map { date ->
        date?.let { BreedingUtil.getExpectedCalvingDate(it) }
    }

    override val calvingStatus = MediatorLiveData<Boolean?>()

    override val calvingDoneOn = MediatorLiveData<LocalDate>()

    init {
        /* Artificial Insemination */

        aiDate.addSource(oldBreeding) {
            aiDate.postValue(it?.artificialInsemination?.date)
        }

        didBy.addSource(oldBreeding) {
            didBy.postValue(it?.artificialInsemination?.didBy)
        }

        bullName.addSource(oldBreeding) {
            bullName.postValue(it?.artificialInsemination?.bullName)
        }

        strawCode.addSource(oldBreeding) {
            strawCode.postValue(it?.artificialInsemination?.strawCode)
        }

        /* Repeat heat */

        repeatHeatStatus.addSource(oldBreeding) {
            repeatHeatStatus.postValue(it?.repeatHeat?.status)
        }

        repeatHeatDoneOn.addSource(oldBreeding) {
            repeatHeatDoneOn.postValue(it?.repeatHeat?.doneOn)
        }

        /* Pregnancy check */

        pregnancyCheckStatus.addSource(oldBreeding) {
            pregnancyCheckStatus.postValue(it?.pregnancyCheck?.status)
        }

        pregnancyCheckDoneOn.addSource(oldBreeding) {
            pregnancyCheckDoneOn.postValue(it?.pregnancyCheck?.doneOn)
        }

        /* Dry off */

        dryOffStatus.addSource(oldBreeding) {
            dryOffStatus.postValue(it?.dryOff?.status)
        }

        dryOffDoneOn.addSource(oldBreeding) {
            dryOffDoneOn.postValue(it?.dryOff?.doneOn)
        }

        /* Calving */

        calvingStatus.addSource(oldBreeding) {
            calvingStatus.postValue(it?.calving?.status)
        }

        calvingDoneOn.addSource(oldBreeding) {
            calvingDoneOn.postValue(it?.calving?.doneOn)
        }
    }
}