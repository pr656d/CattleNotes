/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.timeline

import com.pr656d.model.BreedingWithCattle
import org.threeten.bp.LocalDate

/**
 * Find the first block of each date (rounded down to nearest date) and return pairs of
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
