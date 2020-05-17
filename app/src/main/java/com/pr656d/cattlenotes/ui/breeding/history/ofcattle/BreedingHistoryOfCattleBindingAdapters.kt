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

package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryActionListener
import com.pr656d.model.Breeding

@BindingAdapter(
    value = ["breedingHistoryOfCattleListItems", "breedingHistoryOfCattleViewModel"],
    requireAll = true
)
fun breedingHistoryOfCattleListItems(
    recyclerView: RecyclerView,
    list: List<Breeding>?,
    breedingHistoryOfCattleViewModel: BreedingHistoryOfCattleViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = BreedingHistoryOfCattleAdapter(
            breedingHistoryOfCattleViewModel as BreedingHistoryActionListener
        )
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as BreedingHistoryOfCattleAdapter).submitList(list)
    }
}