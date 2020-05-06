package com.pr656d.cattlenotes.rules

import androidx.test.core.app.ApplicationProvider
import com.pr656d.shared.data.prefs.SharedPreferenceStorage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Rule to be used in tests that sets the SharedPreferences needed for avoiding onboarding flows,
 * resetting filters, etc.
 */
class SetPreferencesRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        SharedPreferenceStorage(ApplicationProvider.getApplicationContext())
            .apply {
            onboardingCompleted = true
            loginCompleted = true
        }
    }
}
