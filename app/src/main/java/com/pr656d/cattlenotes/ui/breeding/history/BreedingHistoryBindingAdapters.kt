package com.pr656d.cattlenotes.ui.breeding.history

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.model.Breeding

@BindingAdapter(value = ["breedingHistoryListItems", "breedingHistoryViewModel"], requireAll = true)
fun breedingHistoryListItems(
    recyclerView: RecyclerView,
    list: List<Breeding>?,
    breedingHistoryViewModel: BreedingHistoryViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = BreedingHistoryAdapter(
            breedingHistoryViewModel as BreedingHistoryActionListener
        )
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as BreedingHistoryAdapter).submitList(list)
    }
}