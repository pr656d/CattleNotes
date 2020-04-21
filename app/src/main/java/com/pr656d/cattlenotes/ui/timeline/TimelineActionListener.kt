package com.pr656d.cattlenotes.ui.timeline

import com.pr656d.model.BreedingWithCattle

interface TimelineActionListener {

    /**
     * On option selected callback.
     */
    fun onOptionSelected(onOptionSelectedData: OnOptionSelectedData)

    /**
     * @param newBreedingWithCattle The new breeding with change and cattle.
     * @param selectedOption Option chosen by user.
     * @param executeOnUndo Execute to undo option selected.
     */
    data class OnOptionSelectedData(
        val oldBreedingWithCattle: BreedingWithCattle,
        val newBreedingWithCattle: BreedingWithCattle,
        val selectedOption: Boolean?,   // null : Neutral, true : Positive, false : Negative
        val executeOnUndo: () -> Unit
    )
}