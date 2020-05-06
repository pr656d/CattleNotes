package com.pr656d.cattlenotes.ui

import android.content.Context
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.R.id
import com.pr656d.cattlenotes.SyncTaskExecutorRule
import com.pr656d.cattlenotes.rules.MainActivityTestRule
import com.pr656d.cattlenotes.rules.SetPreferencesRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Espresso tests for [SettingsFragment]
 */
@RunWith(AndroidJUnit4::class)
class SettingsTest {

    @get:Rule
    var activityRule =
        MainActivityTestRule(id.settingsScreen)

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    // Sets the preferences so no welcome screens are shown
    @get:Rule
    var preferencesRule = SetPreferencesRule()

    private val resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun showSettingsTitle() {
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(id.toolbar))))
            .check(matches(withText(R.string.settings)))
    }
}
