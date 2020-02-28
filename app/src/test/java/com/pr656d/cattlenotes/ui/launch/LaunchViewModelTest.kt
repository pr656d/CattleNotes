package com.pr656d.cattlenotes.ui.launch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.cattlenotes.data.local.prefs.PreferenceStorage
import com.pr656d.cattlenotes.shared.domain.launch.GetLoginCompletedUseCase
import com.pr656d.cattlenotes.test.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MAIN_ACTIVITY
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

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
            on { loginCompleted }.doReturn(false)
        }

        val getLoginCompletedUseCase = GetLoginCompletedUseCase(prefs)
        val viewModel = LaunchViewModel(getLoginCompletedUseCase)

        // When launchDestination is observed
        // Then verify user is navigated to the login activity
        val navigateEvent = LiveDataTestUtil.getValue(viewModel.launchDestination)
        assertEquals(LOGIN_ACTIVITY, navigateEvent?.getContentIfNotHandled())
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
        assertEquals(MAIN_ACTIVITY, navigateEvent?.getContentIfNotHandled())
    }
}