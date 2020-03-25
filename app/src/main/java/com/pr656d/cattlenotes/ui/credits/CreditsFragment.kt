package com.pr656d.cattlenotes.ui.credits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentCreditsBinding
import com.pr656d.cattlenotes.ui.NavigationFragment

class CreditsFragment : NavigationFragment() {

    private lateinit var binding: FragmentCreditsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreditsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}