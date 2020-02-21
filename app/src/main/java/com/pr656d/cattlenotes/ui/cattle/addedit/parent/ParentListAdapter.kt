package com.pr656d.cattlenotes.ui.cattle.addedit.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.databinding.ItemParentCattleBinding

class ParentListAdapter(
    private val parentListDialogViewModel: ParentListDialogViewModel
): ListAdapter<Cattle, ParentListViewHolder>(CattleDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentListViewHolder {
        return ParentListViewHolder(
            ItemParentCattleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parentListDialogViewModel
        )
    }

    override fun onBindViewHolder(holder: ParentListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ParentListViewHolder(
    private val binding: ItemParentCattleBinding,
    private val parentListDialogViewModel: ParentListDialogViewModel
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Cattle) {
        binding.cattle = data
        binding.eventListener = parentListDialogViewModel
        binding.executePendingBindings()
    }
}


object CattleDiff : DiffUtil.ItemCallback<Cattle>() {
    override fun areItemsTheSame(oldItem: Cattle, newItem: Cattle) =
        oldItem.tagNumber == newItem.tagNumber

    override fun areContentsTheSame(oldItem: Cattle, newItem: Cattle) =
        oldItem == newItem
}