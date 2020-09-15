/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ItemBreedingHistoryOfCattleBinding
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryActionListener
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.model.Breeding

class BreedingHistoryOfCattleAdapter(
    private val listener: BreedingHistoryActionListener
) : ListAdapter<Breeding, BreedingHistoryOfCattleListViewHolder>(
    BreedingDiff
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BreedingHistoryOfCattleListViewHolder {
        return BreedingHistoryOfCattleListViewHolder(
            ItemBreedingHistoryOfCattleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: BreedingHistoryOfCattleListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BreedingHistoryOfCattleListViewHolder(
    private val binding: ItemBreedingHistoryOfCattleBinding,
    private val listener: BreedingHistoryActionListener
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var uiBehaviour: ItemBreedingUiBehaviour

    fun bind(data: Breeding) {
        uiBehaviour = ItemBreedingUiBehaviour(data)

        binding.executeAfter {
            breeding = data
            behaviour = uiBehaviour
            listener = this@BreedingHistoryOfCattleListViewHolder.listener
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
     * See for reference [BreedingBehaviour] for [AddEditBreedingFragment] UI behaviour.
     */
    inner class ItemBreedingUiBehaviour(val breeding: Breeding) {

        var expanded = false

        val aiDidByVisibility: Boolean
            get() = breeding.artificialInsemination.didBy.isNullOrEmpty().not()

        val aiBullNameVisibility: Boolean
            get() = breeding.artificialInsemination.bullName.isNullOrEmpty().not()

        val aiStrawCodeVisibility: Boolean
            get() = breeding.artificialInsemination.strawCode.isNullOrEmpty().not()

        val repeatHeatVisibility: Boolean
            get() = expanded

        val repeatHeatDoneOnVisibility: Boolean
            get() = expanded &&
                breeding.repeatHeat.status == true &&
                breeding.repeatHeat.doneOn != null

        val pregnancyCheckVisibility: Boolean
            get() = expanded && breeding.repeatHeat.status == false

        val pregnancyCheckDoneOnVisibility: Boolean
            get() = expanded &&
                breeding.pregnancyCheck.status != null &&
                breeding.pregnancyCheck.doneOn != null

        val dryOffVisibility: Boolean
            get() = expanded && breeding.pregnancyCheck.status == true

        val dryOffDoneOnVisibility: Boolean
            get() = expanded &&
                breeding.dryOff.status == true &&
                breeding.dryOff.doneOn != null

        val calvingVisibility: Boolean
            get() = expanded && breeding.dryOff.status == true

        val calvingDoneOnVisibility: Boolean
            get() = expanded &&
                breeding.calving.status == true &&
                breeding.calving.doneOn != null
    }
}

object BreedingDiff : DiffUtil.ItemCallback<Breeding>() {
    override fun areItemsTheSame(oldItem: Breeding, newItem: Breeding) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Breeding, newItem: Breeding) =
        oldItem == newItem
}
