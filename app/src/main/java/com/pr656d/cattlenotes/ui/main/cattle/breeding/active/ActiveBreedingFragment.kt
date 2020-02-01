package com.pr656d.cattlenotes.ui.main.cattle.breeding.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentActiveBreedingBinding
import dagger.android.support.DaggerFragment

class ActiveBreedingFragment : DaggerFragment() {

    companion object {
        const val TAG = "ActiveBreedingFragment"
    }

    private lateinit var binding: FragmentActiveBreedingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActiveBreedingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}