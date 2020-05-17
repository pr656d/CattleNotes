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

package com.pr656d.cattlenotes.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeObserveUserAuthStateUseCase
import com.pr656d.cattlenotes.test.util.fakes.FakeThemedActivityDelegate
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.domain.auth.ObserveUserAuthStateUseCase
import com.pr656d.shared.domain.result.Result
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [MainViewModel].
 */
class MainViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun createMainViewModel(
        themedActivityDelegate: ThemedActivityDelegate = FakeThemedActivityDelegate(),
        observeUserAuthStateUseCase: ObserveUserAuthStateUseCase
    ): MainViewModel {
        return MainViewModel(themedActivityDelegate, observeUserAuthStateUseCase)
    }

    @Test
    fun userNotSignedIn_redirectToLoginActivity() {
        // Given that is not signed in.
        val viewModel = createMainViewModel(
            observeUserAuthStateUseCase = FakeObserveUserAuthStateUseCase(
                Result.Success(mock { on { isSignedIn() }.doReturn(false) }),
                Result.Success(mock {})
            )
        )

        val redirectToLoginActivity = LiveDataTestUtil.getValue(viewModel.redirectToLoginScreen)
        assertThat(Unit, isEqualTo(redirectToLoginActivity?.getContentIfNotHandled()))
    }

    @Test
    fun firebaseUserIsSignedIn_stayOnMainActivity() {
        // Given that firebase user is signed in.
        val viewModel = createMainViewModel(
            observeUserAuthStateUseCase = FakeObserveUserAuthStateUseCase(
                Result.Success(mock { on { isSignedIn() }.doReturn(true) }),
                Result.Success(mock {})
            )
        )

        val redirectToLoginActivity = LiveDataTestUtil.getValue(viewModel.redirectToLoginScreen)
        assertNull(redirectToLoginActivity?.getContentIfNotHandled())
    }
}