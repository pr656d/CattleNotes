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

package com.pr656d.cattlenotes.ui.launch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.prefs.FakePreferenceStorageRepository
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination
import com.pr656d.shared.domain.login.GetLoginAndAllStepsCompletedUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [LaunchViewModel].
 */
@ExperimentalCoroutinesApi
class LaunchViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Test
    fun notCompletedLogIn_navigateToLoginActivity() = coroutineRule.runBlockingTest {
        // Given that user is *not* logged in and *not* completed first time profile setup.
        val preferenceStorageRepository = object : FakePreferenceStorageRepository() {
            override fun getLoginCompleted(): Boolean = false
            override fun getFirstTimeProfileSetupCompleted(): Boolean = false
        }

        val getLoginAndAllStepsCompletedUseCase =
            GetLoginAndAllStepsCompletedUseCase(
                preferenceStorageRepository,
                coroutineRule.testDispatcher
            )

        val viewModel = LaunchViewModel(getLoginAndAllStepsCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertThat(
            LaunchDestination.LoginActivity,
            isEqualTo(navigateEvent?.getContentIfNotHandled())
        )
    }

    @Test
    fun completedLogInAndFirstTimeProfileSetup_navigateToMainActivity() = coroutineRule.runBlockingTest {
        // Given that user is *not* logged in and *not* completed first time profile setup.
        val preferenceStorageRepository = object : FakePreferenceStorageRepository() {
            override fun getLoginCompleted(): Boolean = true
            override fun getFirstTimeProfileSetupCompleted(): Boolean = true
        }

        val getLoginAndAllStepsCompletedUseCase =
            GetLoginAndAllStepsCompletedUseCase(
                preferenceStorageRepository,
                coroutineRule.testDispatcher
            )

        val viewModel = LaunchViewModel(getLoginAndAllStepsCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertThat(
            LaunchDestination.MainActivity,
            isEqualTo(navigateEvent?.getContentIfNotHandled())
        )
    }

    @Test
    fun notCompletedFirstTimeSetupProfileSetup_navigateToLoginActivity() =
        coroutineRule.runBlockingTest {
            // Given that user is logged in and not completed first time profile setup.
            val preferenceStorageRepository = object : FakePreferenceStorageRepository() {
                override fun getLoginCompleted(): Boolean = true
                override fun getFirstTimeProfileSetupCompleted(): Boolean = false
            }

            val getLoginAndAllStepsCompletedUseCase =
                GetLoginAndAllStepsCompletedUseCase(
                    preferenceStorageRepository,
                    coroutineRule.testDispatcher
                )

            val viewModel = LaunchViewModel(getLoginAndAllStepsCompletedUseCase)

            // When launchDestination is observed
            // Then verify user is navigated to the login activity
            val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
            assertThat(
                LaunchDestination.LoginActivity,
                isEqualTo(navigateEvent?.getContentIfNotHandled())
            )
        }
}