package com.pr656d.cattlenotes.ui.timeline

import androidx.lifecycle.LiveData
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.domain.result.Event

interface TimelineActionListener {
    /**
     * @param newBreedingWithCattle The new breeding with change and cattle.
     * @param selectedOption Option chosen by user.
     */
    fun onOptionSelected(
        oldBreedingWithCattle: BreedingWithCattle,
        newBreedingWithCattle: BreedingWithCattle,
        selectedOption: Boolean?    // null : Neutral, true : Positive, false : Negative
    )

    /**
     * Undo the option selected for the breeding id.
     */
    val undoOptionSelected: LiveData<Event<String>>
}