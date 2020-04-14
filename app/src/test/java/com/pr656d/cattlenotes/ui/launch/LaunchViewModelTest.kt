package com.pr656d.cattlenotes.ui.launch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.login.GetLoginAndAllStepsCompletedUseCase
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [LaunchViewModel].
 */
class LaunchViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @Test
    fun notCompletedLogIn_navigateToLoginActivity() {
        // Given that user is *not* logged in and *not* completed first time profile setup.
        val prefs = mock<PreferenceStorage> {
            on { loginCompleted }.doReturn(false)
            on { firstTimeProfileSetupCompleted }.doReturn(false)
        }

        val getLoginAndAllStepsCompletedUseCase = GetLoginAndAllStepsCompletedUseCase(prefs)
        val viewModel = LaunchViewModel(getLoginAndAllStepsCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertThat(LOGIN_ACTIVITY, isEqualTo(navigateEvent?.getContentIfNotHandled()))
    }

    @Test
    fun notCompletedFirstTimeSetupProfile_navigateToLoginActivity() {
        // Given that user is logged in and not completed first time profile setup.
        val prefs = mock<PreferenceStorage> {
            on { loginCompleted }.doReturn(true)
            on { firstTimeProfileSetupCompleted }.doReturn(false)
        }

        val getLoginAndAllStepsCompletedUseCase = GetLoginAndAllStepsCompletedUseCase(prefs)
        val viewModel = LaunchViewModel(getLoginAndAllStepsCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertThat(LOGIN_ACTIVITY, isEqualTo(navigateEvent?.getContentIfNotHandled()))
    }
}