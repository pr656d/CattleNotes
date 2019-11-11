package com.pr656d.cattlenotes.ui.cattle

import android.view.ViewGroup
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseAdapter

class CattleAdapter(
    private val listener: CattleListClickListener
): BaseAdapter<Cattle, CattleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CattleViewHolder =
        CattleViewHolder(parent, listener)
}