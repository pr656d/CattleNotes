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
package com.pr656d.cattlenotes.ui.milking.list

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.databinding.ItemMilkSourceManualBinding
import com.pr656d.cattlenotes.databinding.ItemMilkSourceSmsBgamamcsBinding
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.model.Milk

sealed class MilkingViewHolder<T : ViewDataBinding> (
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

    class MilkSourceManualViewHolder(
        binding: ItemMilkSourceManualBinding,
        listener: MilkingActionListener
    ) : MilkingViewHolder<ItemMilkSourceManualBinding>(binding, listener)

    class MilkSourceSmsBgamamcsViewHolder(
        binding: ItemMilkSourceSmsBgamamcsBinding,
        listener: MilkingActionListener
    ) : MilkingViewHolder<ItemMilkSourceSmsBgamamcsBinding>(binding, listener)
}
