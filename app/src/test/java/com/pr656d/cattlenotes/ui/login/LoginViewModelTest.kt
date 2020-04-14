package com.pr656d.cattlenotes.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @Test
    fun completeThisTest() {
        TODO("Complete LoginViewModel tests")
    }
}