package com.pr656d.cattlenotes.ui.main.cattle.addedit.parent

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.data.model.Cattle

@BindingAdapter(value = ["parentListItems", "parentListViewModel"], requireAll = true)
fun parentListItems(
    recyclerView: RecyclerView,
    list: List<Cattle>?,
    parentListDialogViewModel: ParentListDialogViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = ParentListAdapter(parentListDialogViewModel)
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as ParentListAdapter).submitList(list)
    }
}