package com.pr656d.cattlenotes.ui.cattle

import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_cattle.view.*

class CattleViewHolder(
    parent: ViewGroup,
    adapter: CattleAdapter
) : BaseItemViewHolder<Cattle>(R.layout.item_cattle, parent) {

    override fun bind(data: Cattle) {
        itemView.tvTagNumber.text = data.tagNumber
        itemView.tvName.text = data.name
        itemView.tvGroup.text = data.group?.displayName
    }

    override fun setupView(view: View) {

    }
}