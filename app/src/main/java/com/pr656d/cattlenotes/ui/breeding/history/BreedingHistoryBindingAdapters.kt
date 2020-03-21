package com.pr656d.cattlenotes.ui.breeding.history

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.model.Breeding

@BindingAdapter(value = ["breedingHistoryListItems"], requireAll = true)
fun breedingHistoryListItems(
    recyclerView: RecyclerView,
    list: List<Breeding>?
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = BreedingHistoryAdapter()
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as BreedingHistoryAdapter).submitList(list)
    }
}