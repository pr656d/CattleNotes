package com.pr656d.cattlenotes.ui.main.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentTimelineBinding
import com.pr656d.cattlenotes.ui.main.NavigationFragment

class TimelineFragment : NavigationFragment() {

    companion object {
        const val TAG = "TimelineFragment"
    }

    private lateinit var binding: FragmentTimelineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}
