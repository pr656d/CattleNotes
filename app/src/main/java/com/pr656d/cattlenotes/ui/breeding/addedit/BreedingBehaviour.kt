package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import javax.inject.Inject

/**
 * UI behaviour for [AddEditBreedingFragment].
 */
interface BreedingBehaviour : BreedingUiDelegate {

    /* Artificial Insemination */

    /**
     * Returns Boolean whether aiDate is available or not.
     */
    val hasAiDate: LiveData<Boolean>

    /**
     * When aiDate is available then show it else hidden.
     */
    val didByVisibility: LiveData<Boolean>

    /**
     * When aiDate is not available reset it.
     */
    val resetDidBy: LiveData<Boolean>

    /**
     * When aiDate is available then show it else hidden.
     */
    val bullNameVisibility: LiveData<Boolean>

    /**
     * When aiDate is not available reset it.
     */
    val resetBullName: LiveData<Boolean>

    /**
     * When aiDate is available then show it else hidden.
     */
    val strawCodeVisibility: LiveData<Boolean>

    /**
     * When aiDate is not available reset it.
     */
    val resetStrawCode: LiveData<Boolean>


    /* Repeat Heat */

    /**
     * When aiDate is available then show it else hidden.
     */
    val repeatHeatTitleVisibility: LiveData<Boolean>

    /**
     * When aiDate is available then show it else hidden.
     */
    val repeatHeatDateExpectedVisibility: LiveData<Boolean>

    /**
     * When aiDate is available then show it else hidden.
     */
    val repeatHeatStatusVisibility: LiveData<Boolean>

    /**
     * When repeat heat status is positive then show it else hidden.
     */
    val repeatHeatDateActualVisibility: LiveData<Boolean>

    /**
     * When repeat heat status is not positive reset it.
     */
    val resetRepeatHeatDateActual: LiveData<Boolean>


    /* Pregnancy Check */

    /**
     * When repeat heat status is negative then show it else hidden.
     */
    val pregnancyCheckTitleVisibility: LiveData<Boolean>

    /**
     * When repeat heat status is negative then show it else hidden.
     */
    val pregnancyCheckDateExpectedVisibility: LiveData<Boolean>

    /**
     * When repeat heat status is negative then show it else hidden.
     */
    val pregnancyCheckStatusVisibility: LiveData<Boolean>

    /**
     * When pregnancy check status is not null then show it else hidden.
     */
    val pregnancyCheckDateActualVisibility: LiveData<Boolean>

    /**
     * When pregnancy check is null then reset it.
     */
    val resetPregnancyCheckDateActual: LiveData<Boolean>


    /* Dry Off */

    /**
     * When pregnancy check status is positive then show it else hidden.
     */
    val dryOffTitleVisibility: LiveData<Boolean>

    /**
     * When pregnancy check status is positive then show it else hidden.
     */
    val dryOffDateExpectedVisibility: LiveData<Boolean>

    /**
     * When pregnancy check status is positive then show it else hidden.
     */
    val dryOffStatusVisibility: LiveData<Boolean>

    /**
     * When dry off status is positive then show it else hidden.
     */
    val dryOffDateActualVisibility: LiveData<Boolean>

    /**
     * When dry off status is not positive then reset it.
     */
    val resetDryOffDateActual: LiveData<Boolean>


    /* Calving */

    /**
     * When dry off status is positive then show it else hidden.
     */
    val calvingTitleVisibility: LiveData<Boolean>

    /**
     * When dry off status is positive then show it else hidden.
     */
    val calvingDateExpectedVisibility: LiveData<Boolean>

    /**
     * When dry off status is positive then show it else hidden.
     */
    val calvingStatusVisibility: LiveData<Boolean>

    /**
     * When calving status is positive or negative then show it else hidden.
     */
    val calvingDateActualVisibility: LiveData<Boolean>

    /**
     * When calving status is not positive then reset it.
     */
    val resetCalvingDateActual: LiveData<Boolean>
}

class BreedingBehaviourImpl @Inject constructor(
    breedingUiDelegate: BreedingUiDelegate
) : BreedingBehaviour,
    BreedingUiDelegate by breedingUiDelegate {

    init {
        /**
         * Reset repeat heat status when aiDate is null.
         */
        repeatHeatStatus.addSource(aiDate) {
            if (it == null) repeatHeatStatus.value = null
        }

        /**
         * Reset pregnancy check status when repeat heat status is null.
         */
        pregnancyCheckStatus.addSource(repeatHeatStatus) {
            if (it == null) pregnancyCheckStatus.value = null
        }

        /**
         * Reset dry off status when pregnancy check status is null.
         */
        dryOffStatus.addSource(pregnancyCheckStatus) {
            if (it == null) dryOffStatus.value = null
        }

        /**
         * Reset calving status when dry off status is null.
         */
        calvingStatus.addSource(dryOffStatus) {
            if (it == null) calvingStatus.value = null
        }
    }

    /* Artificial Insemination */

    /**
     * Returns Boolean whether aiDate is available or not.
     */
    override val hasAiDate: LiveData<Boolean>
        get() = aiDate.map { it != null }

    /**
     * When aiDate is available then show it else hidden.
     */
    override val didByVisibility: LiveData<Boolean>
        get() = hasAiDate

    /**
     * When aiDate is not available reset it.
     */
    override val resetDidBy: LiveData<Boolean>
        get() = hasAiDate.map { !it }

    /**
     * When aiDate is available then show it else hidden.
     */
    override val bullNameVisibility: LiveData<Boolean>
        get() = hasAiDate

    /**
     * When aiDate is not available reset it.
     */
    override val resetBullName: LiveData<Boolean>
        get() = hasAiDate.map { !it }

    /**
     * When aiDate is available then show it else hidden.
     */
    override val strawCodeVisibility: LiveData<Boolean>
        get() = hasAiDate

    /**
     * When aiDate is not available reset it.
     */
    override val resetStrawCode: LiveData<Boolean>
        get() = hasAiDate.map { !it }

    /* Repeat Heat */

    /**
     * When aiDate is available then show it else hidden.
     */
    override val repeatHeatTitleVisibility: LiveData<Boolean>
        get() = hasAiDate

    /**
     * When aiDate is available then show it else hidden.
     */
    override val repeatHeatDateExpectedVisibility: LiveData<Boolean>
        get() = hasAiDate

    /**
     * When aiDate is available then show it else hidden.
     */
    override val repeatHeatStatusVisibility: LiveData<Boolean>
        get() = hasAiDate

    /**
     * When repeat heat status is positive then show it else hidden.
     */
    override val repeatHeatDateActualVisibility: LiveData<Boolean>
        get() = repeatHeatStatus.map { it == true }

    /**
     * When repeat heat status is not positive reset it.
     */
    override val resetRepeatHeatDateActual: LiveData<Boolean>
        get() = repeatHeatStatus.map { it != true }


    /* Pregnancy Check */

    /**
     * When repeat heat status is negative then show it else hidden.
     */
    override val pregnancyCheckTitleVisibility: LiveData<Boolean>
        get() = repeatHeatStatus.map { it == false }

    /**
     * When repeat heat status is negative then show it else hidden.
     */
    override val pregnancyCheckDateExpectedVisibility: LiveData<Boolean>
        get() = repeatHeatStatus.map { it == false }

    /**
     * When repeat heat status is negative then show it else hidden.
     */
    override val pregnancyCheckStatusVisibility: LiveData<Boolean>
        get() = repeatHeatStatus.map { it == false }

    /**
     * When pregnancy check status is not null then show it else hidden.
     */
    override val pregnancyCheckDateActualVisibility: LiveData<Boolean>
        get() = pregnancyCheckStatus.map { it != null }

    /**
     * When pregnancy check is null then reset it.
     */
    override val resetPregnancyCheckDateActual: LiveData<Boolean>
        get() = pregnancyCheckStatus.map { it == null }


    /* Dry Off */

    /**
     * When pregnancy check status is positive then show it else hidden.
     */
    override val dryOffTitleVisibility: LiveData<Boolean>
        get() = pregnancyCheckStatus.map { it == true }

    /**
     * When pregnancy check status is positive then show it else hidden.
     */
    override val dryOffDateExpectedVisibility: LiveData<Boolean>
        get() = pregnancyCheckStatus.map { it == true }

    /**
     * When pregnancy check status is positive then show it else hidden.
     */
    override val dryOffStatusVisibility: LiveData<Boolean>
        get() = pregnancyCheckStatus.map { it == true }

    /**
     * When dry off status is positive then show it else hidden.
     */
    override val dryOffDateActualVisibility: LiveData<Boolean>
        get() = dryOffStatus.map { it == true }

    /**
     * When dry off status is not positive then reset it.
     */
    override val resetDryOffDateActual: LiveData<Boolean>
        get() = dryOffStatus.map { it != true }


    /* Calving */

    /**
     * When dry off status is positive then show it else hidden.
     */
    override val calvingTitleVisibility: LiveData<Boolean>
        get() = dryOffStatus.map { it == true }

    /**
     * When dry off status is positive then show it else hidden.
     */
    override val calvingDateExpectedVisibility: LiveData<Boolean>
        get() = dryOffStatus.map { it == true }

    /**
     * When dry off status is positive then show it else hidden.
     */
    override val calvingStatusVisibility: LiveData<Boolean>
        get() = dryOffStatus.map { it == true }

    /**
     * When calving status is positive or negative then show it else hidden.
     */
    override val calvingDateActualVisibility: LiveData<Boolean>
        get() = calvingStatus.map { it == true }

    /**
     * When calving status is not positive then reset it.
     */
    override val resetCalvingDateActual: LiveData<Boolean>
        get() = calvingStatus.map { it != true }
}