package com.pr656d.cattlenotes.ui.timeline

import com.pr656d.model.BreedingWithCattle

interface TimelineActionListener {

    /**
     * On save clicked callback.
     */
    fun saveBreeding(itemTimelineData: ItemTimelineData, addNewCattle: Boolean = false)

    /**
     * @param newBreedingWithCattle The new breeding with change and cattle.
     * @param selectedOption Option chosen by user.
     */
    data class ItemTimelineData(
        val newBreedingWithCattle: BreedingWithCattle,
        val selectedOption: Boolean?   // null : Neutral, true : Positive, false : Negative
    )
}