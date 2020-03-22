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

    private lateinit var uiBehaviour: ItemBreedingUiBehaviour

    fun bind(data: Breeding) {
        uiBehaviour = ItemBreedingUiBehaviour(data)

        binding.executeAfter {
            breeding = data
            behaviour = uiBehaviour
        }

        itemView.setOnClickListener {
            val parent = itemView.parent as? ViewGroup ?: return@setOnClickListener
            uiBehaviour.expanded = binding.behaviour?.expanded?.not() ?: return@setOnClickListener

            val transition = TransitionInflater.from(itemView.context)
                .inflateTransition(R.transition.expand_shrink_toggle)
            TransitionManager.beginDelayedTransition(parent, transition)

            binding.executeAfter {
                behaviour = uiBehaviour
            }
        }
    }

    /**
     * Holds UI behaviour of item.
     * See for reference [AddEditBreedingFragment] UI behaviour.
     */
    internal class ItemBreedingUiBehaviour(val breeding: Breeding) {

        var expanded = false

        val repeatHeatVisibility: Boolean
            get() = expanded && breeding.artificialInsemination?.date != null

        val repeatHeatDoneOnVisibility: Boolean
            get() = expanded && breeding.repeatHeat?.status == true

        val pregnancyCheckVisibility: Boolean
            get() = expanded && breeding.repeatHeat?.status == false

        val pregnancyCheckDoneOnVisibility: Boolean
            get() = expanded && breeding.pregnancyCheck?.status != null

        val dryOffVisibility: Boolean
            get() = expanded && breeding.pregnancyCheck?.status == true

        val dryOffDoneOnVisibility: Boolean
            get() = expanded && breeding.dryOff?.status == true

        val calvingVisibility: Boolean
            get() = expanded && breeding.dryOff?.status == true

        val calvingDoneOnVisibility: Boolean
            get() = expanded && breeding.calving?.status == true
    }
}

object BreedingDiff : DiffUtil.ItemCallback<Breeding>() {
    override fun areItemsTheSame(oldItem: Breeding, newItem: Breeding) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Breeding, newItem: Breeding) =
        oldItem == newItem
}