package com.pr656d.cattlenotes.ui.breeding.history

import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ItemBreedingHistoryBinding
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.model.Breeding

class BreedingHistoryAdapter : ListAdapter<Breeding, BreedingHistoryListViewHolder>(BreedingDiff) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BreedingHistoryListViewHolder {
        return BreedingHistoryListViewHolder(
            ItemBreedingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BreedingHistoryListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BreedingHistoryListViewHolder(
    private val binding: ItemBreedingHistoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Breeding) {
        binding.executeAfter {
            breeding = data
            binding.isExpanded = isExpanded
        }

        itemView.setOnClickListener {
            val parent = itemView.parent as? ViewGroup ?: return@setOnClickListener
            val expanded = binding.isExpanded ?: false

            val transition = TransitionInflater.from(itemView.context)
                .inflateTransition(R.transition.expand_shrink_toggle)
            TransitionManager.beginDelayedTransition(parent, transition)

            binding.executeAfter {
                isExpanded = !expanded
            }
        }
    }
}


object BreedingDiff : DiffUtil.ItemCallback<Breeding>() {
    override fun areItemsTheSame(oldItem: Breeding, newItem: Breeding) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Breeding, newItem: Breeding) =
        oldItem == newItem
}