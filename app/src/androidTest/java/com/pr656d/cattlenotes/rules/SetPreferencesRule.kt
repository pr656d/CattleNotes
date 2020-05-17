/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.rules

import androidx.test.core.app.ApplicationProvider
import com.pr656d.shared.data.prefs.datasource.SharedPreferenceStorage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Rule to be used in tests that sets the SharedPreferences needed for avoiding onboarding flows,
 * resetting filters, etc.
 */
class SetPreferencesRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        SharedPreferenceStorage(
            ApplicationProvider.getApplicationContext()
        )
            .apply {
            onboardingCompleted = true
            loginCompleted = true
        }
    }
}
