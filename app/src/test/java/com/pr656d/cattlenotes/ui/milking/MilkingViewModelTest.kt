package com.pr656d.cattlenotes.ui.milking

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeMilkRepository
import com.pr656d.cattlenotes.test.util.fakes.FakePreferenceStorageRepository
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.milk.AddAllMilkUseCase
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.LoadAllNewMilkFromSmsUseCase
import com.pr656d.shared.domain.milk.LoadMilkListUseCase
import com.pr656d.shared.domain.milk.sms.GetAvailableMilkSmsSourcesUseCase
import com.pr656d.shared.domain.milk.sms.GetMilkSmsSourceUseCase
import com.pr656d.shared.domain.milk.sms.SetMilkSmsSourceUseCase
import com.pr656d.test.TestData
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
        fakeMilkRepository: MilkRepository = FakeMilkRepository(),
        preferenceStorageRepository: PreferenceStorageRepository = FakePreferenceStorageRepository()
    ): MilkingViewModel {
        return MilkingViewModel(
            loadMilkListUseCase = LoadMilkListUseCase(fakeMilkRepository),
            getAvailableMilkSmsSourcesUseCase = GetAvailableMilkSmsSourcesUseCase(),
            getMilkSmsSourceUseCase = GetMilkSmsSourceUseCase(preferenceStorageRepository),
            setMilkSmsSourceUseCase = SetMilkSmsSourceUseCase(preferenceStorageRepository),
            loadAllNewMilkFromSmsUseCase = LoadAllNewMilkFromSmsUseCase(fakeMilkRepository),
            addMilkUseCase = AddMilkUseCase(fakeMilkRepository),
            addAllMilkUseCase = AddAllMilkUseCase(fakeMilkRepository)
        )
    }

    /*private fun MilkingViewModel.observeUnobserved() {
        loading.observeForever {  }
    }*/

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

    @Test
    fun syncWithSmsMessagesCalledButSmsSourceNotSet_navigateToSmsSourceSelector() {
        val viewModel = createMilkingViewModel()

        // Call sync with messages
        viewModel.syncWithSmsMessages()

        val navigateToSmsSourceSelector = LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)
        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))
    }

    @Test
    fun syncWithSmsMessagesCalledAndSmsSourceIsSet_loadAllNewMilkFromSms() {
        val actualSmsSource = Milk.Source.Sms.BGAMAMCS

        val milkListFromDb = listOf(TestData.milk1, TestData.milk2)

        val milkListFromSms = listOf(
            TestData.milk1, TestData.milk2,
            TestData.milk3, TestData.milk4
        )

        val actualNewMilkListFromSms = listOf(
            TestData.milk3, TestData.milk4
        )

        val fakeMilkRepository: MilkRepository = mock {
            on { getAllMilkUnobserved() }.doReturn(milkListFromDb)
            on { getAllMilkFromSms(actualSmsSource) }.doReturn(milkListFromSms)
        }

        val viewModel = createMilkingViewModel(
            fakeMilkRepository = fakeMilkRepository
        )

        // Call sync with messages  -   smsSource not set yet.
        viewModel.syncWithSmsMessages()

        // Verify navigateToSmsSourceSelector.
        val navigateToSmsSourceSelector = LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)
        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))

        // Set sms source
        viewModel.setSmsSource(actualSmsSource)

        // Verify smsSource is correct.
        val smsSource = viewModel.smsSource
        assertThat(actualSmsSource, isEqualTo(smsSource))

        val newMilkListFromSms = LiveDataTestUtil.getValue(viewModel.newMilkListFromSms)
        assertThat(actualNewMilkListFromSms, isEqualTo(newMilkListFromSms))
    }

    @Test
    fun syncWithSmsMessagesCalledSmsSourceIsAlreadyAvailable_executeLoadAllNewMilkFromSmsUseCase() {
        val actualSmsSource = Milk.Source.Sms.BGAMAMCS

        val viewModel = createMilkingViewModel(
            fakeMilkRepository = mock {
                on { getAllMilkUnobserved() }.doReturn(listOf(
                    TestData.milk1, TestData.milk2
                ))
                on { getAllMilkFromSms(actualSmsSource) }.doReturn(listOf(
                    TestData.milk1, TestData.milk2, TestData.milk3, TestData.milk4
                ))
            },
            preferenceStorageRepository = object : FakePreferenceStorageRepository() {
                override fun getSelectedMilkSmsSource(): Milk.Source.Sms? {
                    return Milk.Source.Sms.BGAMAMCS
                }
            }
        )

        // Call sync with messages  -   sms source is set.
        viewModel.syncWithSmsMessages()

        // Verify smsSource is correct.
        val smsSource = viewModel.smsSource
        assertThat(actualSmsSource, isEqualTo(smsSource))
    }
}