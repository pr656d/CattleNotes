package com.pr656d.cattlenotes.ui.cattle.list

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.data.model.Cattle

@BindingAdapter(value = ["cattleListItems", "cattleListViewModel"], requireAll = true)
fun cattleListItems(
    recyclerView: RecyclerView,
    list: List<Cattle>?,
    cattleListViewModel: CattleListViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = CattleListAdapter(cattleListViewModel)
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as CattleListAdapter).submitList(list)
    }
}