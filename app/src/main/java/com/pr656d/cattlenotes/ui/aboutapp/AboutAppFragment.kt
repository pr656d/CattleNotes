package com.pr656d.cattlenotes.ui.aboutapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentAboutAppBinding
import com.pr656d.cattlenotes.ui.NavigationFragment

class AboutAppFragment : NavigationFragment() {

    companion object {
        const val TAG = "AboutAppFragment"
    }

    private lateinit var binding: FragmentAboutAppBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutAppBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}
