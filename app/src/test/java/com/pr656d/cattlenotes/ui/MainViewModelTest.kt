package com.pr656d.cattlenotes.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.test.util.LiveDataTestUtil
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [MainViewModel].
 */
class MainViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun firebaseUserIsNull_redirectToLoginActivity() {
        // Fake firebase user
        val firebaseUser: FirebaseUser? = null
        // Given that firebase user is *null*.
        val viewModel = MainViewModel(firebaseUser)

        val redirectToLoginActivity = LiveDataTestUtil.getValue(viewModel.redirectToLoginScreen)

        assertEquals(Unit, redirectToLoginActivity?.getContentIfNotHandled())
    }

}