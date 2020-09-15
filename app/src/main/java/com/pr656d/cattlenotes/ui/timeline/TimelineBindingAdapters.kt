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

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.utils.clearDecorations
import com.pr656d.model.BreedingWithCattle

@BindingAdapter(value = ["timelineItems", "timelineViewModel"], requireAll = true)
fun timelineItems(
    recyclerView: RecyclerView,
    list: List<BreedingWithCattle>?,
    timelineViewModel: TimelineViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = TimelineAdapter(timelineViewModel)
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as TimelineAdapter).submitList(list)
    }

    // Recreate the decoration used for the sticky date headers
    recyclerView.clearDecorations()
    if (!list.isNullOrEmpty()) {
        recyclerView.addItemDecoration(
            TimelineHeadersDecoration(recyclerView.context, list)
        )
    }
}
