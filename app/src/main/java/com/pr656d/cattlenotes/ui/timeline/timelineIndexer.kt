package com.pr656d.cattlenotes.ui.timeline

import com.pr656d.model.BreedingWithCattle
import org.threeten.bp.LocalDate

/**
 * Find the first block of each day (rounded down to nearest day) and return pairs of
 * index to start time. Assumes that [timelineItems] are sorted by ascending start time.
 */
fun indexTimelineHeaders(timelineItems: List<BreedingWithCattle>): List<Pair<Int, LocalDate>> {
    return timelineItems
        .mapIndexed { index, block ->
            /** Assumes [block.breeding.getNextBreeding()] is always not null. */
            index to block.breeding.nextBreedingEvent!!.expectedOn
        }
        .distinctBy { it.second }
}
