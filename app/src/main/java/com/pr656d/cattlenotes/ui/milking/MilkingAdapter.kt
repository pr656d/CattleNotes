package com.pr656d.cattlenotes.ui.milking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pr656d.cattlenotes.databinding.ItemMilkSourceManualBinding
import com.pr656d.cattlenotes.databinding.ItemMilkSourceSmsBgamamcsBinding
import com.pr656d.cattlenotes.ui.milking.MilkingViewHolder.MilkSourceManualViewHolder
import com.pr656d.cattlenotes.ui.milking.MilkingViewHolder.MilkSourceSmsBgamamcsViewHolder
import com.pr656d.model.Milk

class MilkingAdapter(
    private val milkingViewModel: MilkingViewModel
) : ListAdapter<Milk, MilkingViewHolder<*>>(MilkDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MilkingViewHolder<*> =
        when(getItem(position).source) {
            Milk.Source.Manual -> MilkSourceManualViewHolder(
                ItemMilkSourceManualBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                milkingViewModel
            )
            Milk.Source.Sms.BGAMAMCS -> MilkSourceSmsBgamamcsViewHolder(
                ItemMilkSourceSmsBgamamcsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                milkingViewModel
            )
        }

    override fun onBindViewHolder(holder: MilkingViewHolder<*>, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Return position. We will handle it in onCreateViewHolder.
     */
    override fun getItemViewType(position: Int): Int = position
}

object MilkDiff : DiffUtil.ItemCallback<Milk>() {
    override fun areItemsTheSame(oldItem: Milk, newItem: Milk): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Milk, newItem: Milk): Boolean = oldItem == newItem
}