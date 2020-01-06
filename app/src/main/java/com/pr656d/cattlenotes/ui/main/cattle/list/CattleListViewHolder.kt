package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_cattle.view.*

class CattleListViewHolder(
    parent: ViewGroup
) : BaseItemViewHolder<Cattle>(R.layout.item_cattle, parent) {

    private lateinit var tagNumber: String

    override fun bind(data: Cattle) {
        // Save to use for click listener
        tagNumber = data.tagNumber.toString()

        val setTextIfDataAvailableElseHideView: TextView.(s: String?) -> Unit = { s: String? ->
            if (s.isNullOrBlank()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = s
            }
        }

        // Bind views
        itemView.tvTagNumber.setTextIfDataAvailableElseHideView(data.tagNumber.toString())
        itemView.tvName.setTextIfDataAvailableElseHideView(data.name)
        itemView.tvGroup.setTextIfDataAvailableElseHideView(data.group.displayName)
        itemView.tvType.setTextIfDataAvailableElseHideView(data.type.displayName)
    }

    override fun setupView(view: View) {
        view.layout_cattle_item.setOnClickListener {
            val action = CattleListFragmentDirections.navigateToCattleDetails(tagNumber)
            it.findNavController().navigate(action)
        }
    }
}