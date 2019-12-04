package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.ViewGroup
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseAdapter

class CattleListAdapter(
    private val listener: CattleListClickListener
): BaseAdapter<Cattle, CattleListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CattleListViewHolder =
        CattleListViewHolder(
            parent,
            listener
        )
}