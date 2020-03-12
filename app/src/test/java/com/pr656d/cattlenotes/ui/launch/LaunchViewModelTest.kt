package com.pr656d.cattlenotes.ui.launch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.launch.GetLoginCompletedUseCase
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
    fun notCompletedLogIn_navigatesToLoginActivity() {
        // Given that user is *not* logged in.
        val prefs = mock<PreferenceStorage> {
            on { loginCompleted }.thenReturn(false)
        }

        val getLoginCompletedUseCase = GetLoginCompletedUseCase(prefs)
        val viewModel = LaunchViewModel(getLoginCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertThat(LOGIN_ACTIVITY, isEqualTo(navigateEvent?.getContentIfNotHandled()))
    }

    @Test
    fun completedLogIn_navigatesToMainActivity() {
        // Given that user is *not* logged in.
        val prefs = mock<PreferenceStorage> {
            on { loginCompleted }.doReturn(true)
        }
        
        val getLoginCompletedUseCase = GetLoginCompletedUseCase(prefs)
        val viewModel = LaunchViewModel(getLoginCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertThat(MAIN_ACTIVITY, isEqualTo(navigateEvent?.getContentIfNotHandled()))
    }
}