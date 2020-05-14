package com.pr656d.cattlenotes.ui.milking

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pr656d.cattlenotes.ui.milking.viewholder.MilkingViewHolder
import com.pr656d.model.Milk

class MilkingAdapter(
    private val milkingViewModel: MilkingViewModel
) : ListAdapter<Milk, MilkingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilkingViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MilkingViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        TODO("Not yet implemented")
    }
}

object BreedingWithCattleDiff : DiffUtil.ItemCallback<Milk>() {
    override fun areItemsTheSame(oldItem: Milk, newItem: Milk): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Milk, newItem: Milk): Boolean =
        oldItem == newItem
}