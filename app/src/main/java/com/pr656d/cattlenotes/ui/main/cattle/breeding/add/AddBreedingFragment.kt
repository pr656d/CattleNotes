package com.pr656d.cattlenotes.ui.main.cattle.breeding.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentAddBreedingBinding
import dagger.android.support.DaggerFragment

class AddBreedingFragment : DaggerFragment() {

    companion object {
        const val TAG = "AddBreedingFragment"
    }

    private lateinit var binding: FragmentAddBreedingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBreedingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}