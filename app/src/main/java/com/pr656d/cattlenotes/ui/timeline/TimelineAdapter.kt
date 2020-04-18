package com.pr656d.cattlenotes.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.databinding.ItemTimelineBinding
import com.pr656d.model.BreedingWithCattle

class TimelineAdapter(
    private val timelineViewModel: TimelineViewModel
): ListAdapter<BreedingWithCattle, TimelineViewHolder>(BreedingWithCattleDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return TimelineViewHolder(
            ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            timelineViewModel
        )
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TimelineViewHolder(
    private val binding: ItemTimelineBinding,
    private val timelineViewModel: TimelineViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: BreedingWithCattle) {
        binding.cattle = data.cattle
        binding.breedingEvent = data.breeding.getNextBreedingEvent()
        binding.title = "${data.breeding.getNextBreedingEvent()?.type?.displayName} ( ${data.cattle.name ?: data.cattle.tagNumber} )"
        binding.executePendingBindings()
    }
}

object BreedingWithCattleDiff : DiffUtil.ItemCallback<BreedingWithCattle>() {
    override fun areItemsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem.cattle.id == newItem.cattle.id && oldItem.breeding.id == newItem.breeding.id

    override fun areContentsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem == newItem
}