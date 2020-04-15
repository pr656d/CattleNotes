package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeProfileDelegate
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

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun createAddEditProfileViewModel(): AddEditProfileViewModel {
        return AddEditProfileViewModel(FakeProfileDelegate()).apply { observeUnobserved() }
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
    fun saveProfileCalled_navigateUpOnSuccess() {
        val viewModel = createAddEditProfileViewModel()

        // Call save profile
        viewModel.saveProfile()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }
}