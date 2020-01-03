package com.pr656d.cattlenotes.ui.main.cattle.parent

import android.view.ViewGroup
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseAdapter

class ParentCattleListAdapter(
    private val callback: ClickListener
) : BaseAdapter<Cattle, ParentCattleListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentCattleListViewHolder =
        ParentCattleListViewHolder(parent, callback)

    interface ClickListener {
        fun onClick(parentTagNumber: String)
    }
}