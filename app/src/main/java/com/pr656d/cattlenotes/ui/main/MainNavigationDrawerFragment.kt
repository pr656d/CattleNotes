package com.pr656d.cattlenotes.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import kotlinx.android.synthetic.main.fragment_main_navigation_drawer.*

class MainNavigationDrawerFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "MainNavigationDrawerFragment"

        fun newInstance(): MainNavigationDrawerFragment = MainNavigationDrawerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_main_navigation_drawer, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        nvMain.apply {
            setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.itemSettings -> {
                        Toaster.show(context, "Settings")
                        true
                    }
                    R.id.itemAboutApp -> {
                        Toaster.show(this.context, "About this app")
                        true
                    }
                    R.id.itemOpenSourceLicenses -> {
                        Toaster.show(this.context, "Open-source licenses")
                        true
                    }
                    else -> false
                }
            }
        }
    }
}