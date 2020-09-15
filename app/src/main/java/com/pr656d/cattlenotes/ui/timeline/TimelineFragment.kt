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
package com.pr656d.cattlenotes.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pr656d.cattlenotes.databinding.FragmentTimelineBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.timeline.TimelineFragmentDirections.Companion.toAddEditCattle
import com.pr656d.shared.domain.result.EventObserver
import timber.log.Timber
import javax.inject.Inject

class TimelineFragment : NavigationFragment() {

    companion object {
        const val TAG = "TimelineFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<TimelineViewModel> { viewModelFactory }
    private lateinit var binding: FragmentTimelineBinding
    private val args by navArgs<TimelineFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.breedingId?.let {
            Timber.d("Timeline get the breeding $it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchAddNewCattleScreen.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(toAddEditCattle(parentId = it.id))
            }
        )
    }
}
