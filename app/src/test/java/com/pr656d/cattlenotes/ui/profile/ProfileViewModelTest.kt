package com.pr656d.cattlenotes.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeProfileDelegate
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

class ProfileViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun createProfileViewModel() : ProfileViewModel {
        return ProfileViewModel(FakeProfileDelegate())
    }

    @Test
    fun logoutCalled_showLogoutConfirmation() {
        val viewModel = createProfileViewModel()

        // Call logout
        viewModel.logout()

        val showLogoutConfirmation = LiveDataTestUtil.getValue(viewModel.showLogoutConfirmation)
        assertThat(Unit, isEqualTo(showLogoutConfirmation?.getContentIfNotHandled()))
    }

    @Test
    fun logoutCalledAndLogoutConfirmationIsTrue_launchLogout() {
        val viewModel = createProfileViewModel()

        // Call logout
        viewModel.logout(true)

        val launchLogout = LiveDataTestUtil.getValue(viewModel.launchLogout)
        assertThat(Unit, isEqualTo(launchLogout?.getContentIfNotHandled()))
    }

    @Test
    fun editProfileCalled_launchEditProfile() {
        val viewModel = createProfileViewModel()

        // Call edit profile
        viewModel.editProfile()

        val launchEditProfile = LiveDataTestUtil.getValue(viewModel.launchEditProfile)
        assertThat(Unit, isEqualTo(launchEditProfile?.getContentIfNotHandled()))
    }
}