/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.FakeProfileDelegate
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [AddEditProfileViewModel].
 */
class AddEditProfileViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private fun createAddEditProfileViewModel(): AddEditProfileViewModel {
        return AddEditProfileViewModel(
            FakeProfileDelegate(coroutineDispatcher = coroutineRule.testDispatcher)
        ).apply { observeUnobserved() }
    }

    private fun AddEditProfileViewModel.observeUnobserved() {
        // Profile Delegate
        currentUserInfo.observeForever { }
        farmName.observeForever { }
        dairyCode.observeForever { }
        dairyCustomerId.observeForever { }
        farmAddress.observeForever { }
        imageUrl.observeForever { }
        name.observeForever { }
        email.observeForever { }
        phoneNumber.observeForever { }
        dob.observeForever { }
        address.observeForever { }
    }

    @Test
    fun saveProfileCalled_navigateUpOnSuccess() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditProfileViewModel()

        // Call save profile
        viewModel.save()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }
}
