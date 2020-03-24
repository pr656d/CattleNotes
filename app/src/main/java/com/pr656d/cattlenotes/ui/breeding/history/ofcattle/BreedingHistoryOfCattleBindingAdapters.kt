package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryActionListener
import com.pr656d.model.Breeding

@BindingAdapter(
    value = ["breedingHistoryOfCattleListItems", "breedingHistoryOfCattleViewModel"],
    requireAll = true
)
fun breedingHistoryOfCattleListItems(
    recyclerView: RecyclerView,
    list: List<Breeding>?,
    breedingHistoryOfCattleViewModel: BreedingHistoryOfCattleViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = BreedingHistoryOfCattleAdapter(
            breedingHistoryOfCattleViewModel as BreedingHistoryActionListener
        )
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as BreedingHistoryOfCattleAdapter).submitList(list)
    }
}