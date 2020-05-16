package com.pr656d.cattlenotes.ui.milking.list

import com.pr656d.model.Milk
import org.threeten.bp.ZonedDateTime

/**
 * Find the first block of each date (rounded down to nearest date) and return pairs of
 * index to start time. Assumes that [milkingItems] are sorted by time recent to oldest.
 */
fun indexMilkingHeaders(milkingItems: List<Milk>): List<Pair<Int, ZonedDateTime>> {
    return milkingItems
        .mapIndexed { index, block ->
            index to block.timestamp
        }
        .distinctBy { it.second.toLocalDate() } // Group by date.
}
