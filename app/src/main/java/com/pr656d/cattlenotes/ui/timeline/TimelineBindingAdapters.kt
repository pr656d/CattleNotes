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
    list ?: return

    if (recyclerView.adapter == null) {
        recyclerView.adapter = TimelineAdapter(timelineViewModel)
    }

    if (list.isEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as TimelineAdapter).submitList(list)
    }

    // Recreate the decoration used for the sticky date headers
    recyclerView.clearDecorations()
    if (list.isNotEmpty()) {
        recyclerView.addItemDecoration(
            TimelineHeadersDecoration(recyclerView.context, list)
        )
    }
}