/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.milking.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pr656d.cattlenotes.databinding.ItemMilkSourceManualBinding
import com.pr656d.cattlenotes.databinding.ItemMilkSourceSmsBgamamcsBinding
import com.pr656d.cattlenotes.ui.milking.list.MilkingViewHolder.MilkSourceManualViewHolder
import com.pr656d.cattlenotes.ui.milking.list.MilkingViewHolder.MilkSourceSmsBgamamcsViewHolder
import com.pr656d.model.Milk

class MilkingAdapter(
    private val milkingViewModel: MilkingViewModel
) : ListAdapter<Milk, MilkingViewHolder<*>>(
    MilkDiff
) {

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