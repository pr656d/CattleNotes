package com.pr656d.cattlenotes.ui.milking

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.shared.domain.milk.LoadAllNewMilkFromSmsUseCase
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [MilkingViewModel].
 */
class MilkingViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun createMilkingViewModel(
        loadAllNewMilkFromSmsUseCase: LoadAllNewMilkFromSmsUseCase = mock {  }
    ): MilkingViewModel {
        return MilkingViewModel(loadAllNewMilkFromSmsUseCase)
    }

    @Test
    fun requestPermissionCalled_requestPermissions() {
        val viewModel = createMilkingViewModel()

        // Call request permission
        viewModel.requestPermission()

        val requestPermissions = LiveDataTestUtil.getValue(viewModel.requestPermissions)
        assertThat(Unit, isEqualTo(requestPermissions?.getContentIfNotHandled()))
    }

    @Test
    fun setPermissionsGranted() {
        val viewModel = createMilkingViewModel()

        // Call request permission
        viewModel.requestPermission()

        // Set granted as true
        viewModel.setPermissionsGranted(true)

        assertTrue(LiveDataTestUtil.getValue(viewModel.permissionsGranted) ?: false)
    }
}