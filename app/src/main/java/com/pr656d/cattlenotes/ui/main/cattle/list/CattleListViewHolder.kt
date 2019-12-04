package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_cattle.view.*

class CattleListViewHolder(
    parent: ViewGroup,
    private val listener: CattleListClickListener
) : BaseItemViewHolder<Cattle>(R.layout.item_cattle, parent) {

    private lateinit var cattle: Cattle

    override fun bind(data: Cattle) {
        // Save to use for click listener
        cattle = data

        // Bind views
        itemView.tvTagNumber.text = data.tagNumber
        itemView.tvName.text = data.name
        itemView.tvGroup.text = data.group
        itemView.tvType.text = data.type
    }

    override fun setupView(view: View) {
        view.layout_cattle_item.setOnClickListener {
            listener.onClick(cattle)
        }
    }
}