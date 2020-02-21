package com.pr656d.cattlenotes.ui.cattle.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.databinding.ItemCattleBinding

class CattleListAdapter(
    private val cattleListViewModel: CattleListViewModel
): ListAdapter<Cattle, CattleListViewHolder>(CattleDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CattleListViewHolder {
        return CattleListViewHolder(
            ItemCattleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            cattleListViewModel
        )
    }

    override fun onBindViewHolder(holder: CattleListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CattleListViewHolder(
    private val binding: ItemCattleBinding,
    private val cattleListViewModel: CattleListViewModel
) : RecyclerView.ViewHolder(binding.root) {

    private var tagNumber: Long = 0

    fun bind(data: Cattle) {
        binding.cattle = data
        binding.eventListener = cattleListViewModel
        tagNumber = data.tagNumber
        binding.executePendingBindings()
    }
}


object CattleDiff : DiffUtil.ItemCallback<Cattle>() {
    override fun areItemsTheSame(oldItem: Cattle, newItem: Cattle) =
        oldItem.tagNumber == newItem.tagNumber

    override fun areContentsTheSame(oldItem: Cattle, newItem: Cattle) =
        oldItem == newItem
}