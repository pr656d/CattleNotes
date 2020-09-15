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
package com.pr656d.cattlenotes.ui.cattle.addedit.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.databinding.ItemParentCattleBinding
import com.pr656d.cattlenotes.ui.cattle.addedit.AddEditCattleViewModel
import com.pr656d.model.Cattle

class ParentListAdapter(
    private val parentListDialogViewModel: AddEditCattleViewModel
) : ListAdapter<Cattle, ParentListViewHolder>(CattleDiff) {

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
    private val addEditCattleViewModel: AddEditCattleViewModel
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Cattle) {
        binding.cattle = data
        binding.eventListener = addEditCattleViewModel
        binding.executePendingBindings()
    }
}

object CattleDiff : DiffUtil.ItemCallback<Cattle>() {
    override fun areItemsTheSame(oldItem: Cattle, newItem: Cattle) =
        oldItem.tagNumber == newItem.tagNumber

    override fun areContentsTheSame(oldItem: Cattle, newItem: Cattle) =
        oldItem == newItem
}
