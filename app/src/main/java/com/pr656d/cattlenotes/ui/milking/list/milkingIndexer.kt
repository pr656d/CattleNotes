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
