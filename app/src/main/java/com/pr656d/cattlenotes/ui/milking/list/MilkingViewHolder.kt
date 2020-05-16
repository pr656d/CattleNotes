package com.pr656d.cattlenotes.ui.milking.list

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.databinding.ItemMilkSourceManualBinding
import com.pr656d.cattlenotes.databinding.ItemMilkSourceSmsBgamamcsBinding
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.model.Milk

sealed class MilkingViewHolder<T: ViewDataBinding> (
    private val binding: T,
    private val listener: MilkingActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(milk: Milk) {
        binding.executeAfter {
            setVariable(BR.data, milk)
        }
    }

    fun onDeleteClick(milk: Milk) {
        listener.delete(milk)
    }

    class MilkSourceManualViewHolder (
        binding: ItemMilkSourceManualBinding,
        listener: MilkingActionListener
    ) : MilkingViewHolder<ItemMilkSourceManualBinding>(binding, listener)

    class MilkSourceSmsBgamamcsViewHolder (
        binding: ItemMilkSourceSmsBgamamcsBinding,
        listener: MilkingActionListener
    ) : MilkingViewHolder<ItemMilkSourceSmsBgamamcsBinding>(binding, listener)
}