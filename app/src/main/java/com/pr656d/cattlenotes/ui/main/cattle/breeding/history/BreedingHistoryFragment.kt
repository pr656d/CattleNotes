package com.pr656d.cattlenotes.ui.main.cattle.breeding.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentBreedingHistoryBinding
import com.pr656d.cattlenotes.ui.main.NavigationFragment

class BreedingHistoryFragment : NavigationFragment() {

    companion object {
        const val TAG = "BreedingHistoryFragment"
    }

    private lateinit var binding: FragmentBreedingHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreedingHistoryBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}