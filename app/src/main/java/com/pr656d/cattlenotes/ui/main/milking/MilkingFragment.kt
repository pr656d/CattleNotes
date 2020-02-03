package com.pr656d.cattlenotes.ui.main.milking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentMilkingBinding
import com.pr656d.cattlenotes.ui.main.NavigationFragment

class MilkingFragment : NavigationFragment() {

    companion object {
        const val TAG = "MilkingFragment"
    }

    private lateinit var binding: FragmentMilkingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMilkingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}
