package com.pr656d.cattlenotes.ui.cashflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pr656d.cattlenotes.databinding.FragmentCashflowBinding
import com.pr656d.cattlenotes.ui.NavigationFragment

class CashflowFragment : NavigationFragment() {

    companion object {
        const val TAG = "CashflowFragment"
    }

    private lateinit var binding: FragmentCashflowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashflowBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}
