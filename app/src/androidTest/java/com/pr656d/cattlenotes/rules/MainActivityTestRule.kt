package com.pr656d.cattlenotes.rules

import android.content.Intent
import androidx.test.rule.ActivityTestRule
import com.pr656d.cattlenotes.ui.MainActivity

/**
 * ActivityTestRule for [MainActivity] that can launch with any initial navigation target.
 */
class MainActivityTestRule(
    private val initialNavId: Int
) : ActivityTestRule<MainActivity>(MainActivity::class.java) {

    override fun getActivityIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            putExtra(MainActivity.EXTRA_NAVIGATION_ID, initialNavId)
        }
    }
}
