package com.pr656d.cattlenotes.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.test.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeThemedActivityDelegate
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

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
        firebaseUser: FirebaseUser?,
        themedActivityDelegate: ThemedActivityDelegate = FakeThemedActivityDelegate()
    ): MainViewModel {
        return MainViewModel(
            firebaseUser = firebaseUser,
            themedActivityDelegate = themedActivityDelegate
        )
    }

    @Test
    fun firebaseUserIsNull_redirectToLoginActivity() {
        // If we get firebaseUser as null
        val firebaseUser: FirebaseUser? = null
        // Given that firebase user is *null*.
        val viewModel = createMainViewModel(firebaseUser)

        val redirectToLoginActivity = LiveDataTestUtil.getValue(viewModel.redirectToLoginScreen)

        assertEquals(Unit, redirectToLoginActivity?.getContentIfNotHandled())
    }

    @Test
    fun firebaseUserIsNotNull_stayOnMainActivity() {
        // If we get firebaseUser as null
        val firebaseUser: FirebaseUser = mock(FirebaseUser::class.java)
        // Given that firebase user is *null*.
        val viewModel = createMainViewModel(firebaseUser)

        val redirectToLoginActivity = LiveDataTestUtil.getValue(viewModel.redirectToLoginScreen)

        assertEquals(null, redirectToLoginActivity?.getContentIfNotHandled())
    }

}