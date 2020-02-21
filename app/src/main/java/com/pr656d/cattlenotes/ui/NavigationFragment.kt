package com.pr656d.cattlenotes.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.pr656d.cattlenotes.R
import dagger.android.support.DaggerFragment

/**
 * To be implemented by components that host top-level navigation destinations.
 */
interface NavigationHost {

    /** Called by MainNavigationFragment to setup it's toolbar with the navigation controller. */
    fun registerToolbarWithNavigation(toolbar: Toolbar)
}

/**
 * Fragment representing a navigation destination. This class handles wiring up the [Toolbar]
 * navigation icon if the fragment is attached to a [NavigationHost].
 */
open class NavigationFragment : DaggerFragment() {

    protected var navigationHost: NavigationHost? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigationHost = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // If we have a toolbar and we are attached to a proper navigation host, set up the toolbar
        // navigation icon.
        val host = navigationHost ?: return
        val mainToolbar: Toolbar = view.findViewById(R.id.toolbar) ?: return
        mainToolbar.apply {
            host.registerToolbarWithNavigation(this)
        }
    }
}
