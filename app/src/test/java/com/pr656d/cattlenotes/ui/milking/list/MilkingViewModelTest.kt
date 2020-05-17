/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.milking.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeMilkRepository
import com.pr656d.cattlenotes.test.util.fakes.FakePreferenceStorageRepository
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.milk.AddAllMilkUseCase
import com.pr656d.shared.domain.milk.LoadAllNewMilkFromSmsUseCase
import com.pr656d.shared.domain.milk.LoadMilkListUseCase
import com.pr656d.shared.domain.milk.sms.GetAvailableMilkSmsSourcesUseCase
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.milk.sms.SetPreferredMilkSmsSourceUseCase
import com.pr656d.test.TestData
import org.junit.Assert.*
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

    private val milkRepository = object : FakeMilkRepository() {
        override fun getAllMilk(): LiveData<List<Milk>> {
            return MutableLiveData(emptyList())
        }
    }

    private fun createMilkingViewModel(
        fakeMilkRepository: MilkRepository = milkRepository,
        fakePreferenceStorageRepository: PreferenceStorageRepository = FakePreferenceStorageRepository()
    ): MilkingViewModel {
        return MilkingViewModel(
            loadMilkListUseCase = LoadMilkListUseCase(fakeMilkRepository),
            getAvailableMilkSmsSourcesUseCase = GetAvailableMilkSmsSourcesUseCase(),
            getPreferredMilkSmsSourceUseCase = GetPreferredMilkSmsSourceUseCase(fakePreferenceStorageRepository),
            setPreferredMilkSmsSourceUseCase = SetPreferredMilkSmsSourceUseCase(fakePreferenceStorageRepository),
            loadAllNewMilkFromSmsUseCase = LoadAllNewMilkFromSmsUseCase(fakeMilkRepository),
            addAllMilkUseCase = AddAllMilkUseCase(fakeMilkRepository)
        ).apply { observeUnobserved() }
    }

    private fun MilkingViewModel.observeUnobserved() {
        loading.observeForever { }
        milkList.observeForever { }
        smsSource.observeForever {  }
        newMilkListFromSms.observeForever { }
        permissionsGranted.observeForever { }
        showPermissionExplanation.observeForever { }
        availableMilkSmsSources.observeForever { }
        navigateToAddMilk.observeForever {  }
        navigateToSmsSourceSelector.observeForever {  }
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

    @Test
    fun initiallyPermissionNotGranted() {
        val viewModel = createMilkingViewModel()

        // Assume we get false initially.
        viewModel.setPermissionsGranted(false)

        assertFalse(LiveDataTestUtil.getValue(viewModel.permissionsGranted)!!)
    }

    @Test
    fun requestForPermissionCalledAndPermissionGrantedButSmsSourceNotSet_navigateToSmsSourceSelector() {
        val viewModel = createMilkingViewModel()

        // Request for permission.
        viewModel.requestPermission()

        // Grant permission
        viewModel.setPermissionsGranted(true)

        val navigateToSmsSourceSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)
        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))
    }

    @Test
    fun requestForPermissionCalledAndPermissionGrantedAndSmsSourceIsSet_loadAllNewMilkFromSms() {
        val actualSmsSource = Milk.Source.Sms.BGAMAMCS
        val actualNewMilkListFromSms = TestData.milkList

        val fakeMilkRepository: MilkRepository = object : FakeMilkRepository() {
            override fun getAllMilk(): LiveData<List<Milk>> = MutableLiveData(emptyList())

            override fun getAllMilkUnobserved(): List<Milk> = emptyList()

            override fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> =
                TestData.milkList
        }

        val viewModel = createMilkingViewModel(
            fakeMilkRepository = fakeMilkRepository
        )

        // Request for permission.
        viewModel.requestPermission()

        // Grant permission
        viewModel.setPermissionsGranted(true)

        // Set sms source
        viewModel.setSmsSource(actualSmsSource)

        val newMilkListFromSms = LiveDataTestUtil.getValue(viewModel.newMilkListFromSms)
        assertThat(actualNewMilkListFromSms, isEqualTo(newMilkListFromSms))
    }

    /**
     * Sync with sms messages is called but SMS permission is not granted and SMS source is not set.
     * Ask for permission, grant permission, navigate to sms source selector, set sms source and
     * start sync with sms messages.
     */
    @Test
    fun syncWithSmsMessagesCalledInitially() {
        val actualSmsSource = Milk.Source.Sms.BGAMAMCS

        val fakeMilkRepository: MilkRepository = object : FakeMilkRepository() {
            override fun getAllMilk(): LiveData<List<Milk>> = MutableLiveData(emptyList())

            override fun getAllMilkUnobserved(): List<Milk> = emptyList()

            override fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> =
                TestData.milkList
        }

        val actualNewMilkListFromSms = TestData.milkList

        val viewModel = createMilkingViewModel(
            fakeMilkRepository = fakeMilkRepository
        )

        // Set initial permission granted to false.
        viewModel.setPermissionsGranted(false)

        // Call sync with sms messages
        viewModel.syncWithSmsMessages()

        // Check permission asked.
        assertThat(
            Unit,
            isEqualTo(
                LiveDataTestUtil
                    .getValue(viewModel.requestPermissions)
                    ?.getContentIfNotHandled()
            )
        )

        // Permission granted.
        viewModel.setPermissionsGranted(true)

        // Check asked to select sms source.
        assertThat(
            Unit,
            isEqualTo(
                LiveDataTestUtil
                    .getValue(viewModel.navigateToSmsSourceSelector)
                    ?.getContentIfNotHandled()
            )
        )

        // Sms source is set.
        viewModel.setSmsSource(actualSmsSource)

        val newMilkListFromSms = LiveDataTestUtil.getValue(viewModel.newMilkListFromSms)
        assertThat(actualNewMilkListFromSms, isEqualTo(newMilkListFromSms))
    }

    @Test
    fun syncWithSmsMessagesCalledButSmsSourceNotSet_navigateToSmsSourceSelector() {
        val viewModel = createMilkingViewModel()

        // Call sync with messages
        viewModel.syncWithSmsMessages()

        val navigateToSmsSourceSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)
        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))
    }

    @Test
    fun syncWithSmsMessagesCalledAndSmsSourceIsSet_loadAllNewMilkFromSms() {
        val actualSmsSource = Milk.Source.Sms.BGAMAMCS

        val fakeMilkRepository: MilkRepository = object : FakeMilkRepository() {
            override fun getAllMilk(): LiveData<List<Milk>> = MutableLiveData(emptyList())

            override fun getAllMilkUnobserved(): List<Milk> = listOf(TestData.milk1, TestData.milk2)

            override fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> = listOf(
                TestData.milk1, TestData.milk2,
                TestData.milk3, TestData.milk4
            )
        }

        val actualNewMilkListFromSms = listOf(
            TestData.milk3, TestData.milk4
        )

        val viewModel = createMilkingViewModel(
            fakeMilkRepository = fakeMilkRepository
        )

        // Call sync with messages  -   smsSource not set yet.
        viewModel.syncWithSmsMessages()

        // Verify navigateToSmsSourceSelector.
        val navigateToSmsSourceSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)
        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))

        // Set sms source
        viewModel.setSmsSource(actualSmsSource)

        // Verify smsSource is correct.
        val smsSource = LiveDataTestUtil.getValue(viewModel.smsSource)
        assertThat(actualSmsSource, isEqualTo(smsSource))

        val newMilkListFromSms = LiveDataTestUtil.getValue(viewModel.newMilkListFromSms)
        assertThat(actualNewMilkListFromSms, isEqualTo(newMilkListFromSms))
    }

    @Test
    fun syncWithSmsMessagesCalledSmsSourceIsAlreadyAvailable_loadAllNewMilkFromSms() {
        val actualSmsSource = Milk.Source.Sms.BGAMAMCS

        val fakeMilkRepository: MilkRepository = object : FakeMilkRepository() {
            override fun getAllMilk(): LiveData<List<Milk>> = MutableLiveData(emptyList())

            override fun getAllMilkUnobserved(): List<Milk> = listOf(TestData.milk1, TestData.milk2)

            override fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> = listOf(
                TestData.milk1, TestData.milk2,
                TestData.milk3, TestData.milk4
            )
        }

        val actualNewMilkListFromSms = listOf(
            TestData.milk3, TestData.milk4
        )

        val fakePreferenceStorageRepository = object : FakePreferenceStorageRepository() {
            override fun getPreferredMilkSmsSource(): Milk.Source.Sms? {
                return Milk.Source.Sms.BGAMAMCS
            }
        }

        val viewModel = createMilkingViewModel(
            fakeMilkRepository = fakeMilkRepository,
            fakePreferenceStorageRepository = fakePreferenceStorageRepository
        )

        // Call sync with messages  -   sms source is set.
        viewModel.syncWithSmsMessages()

        // Verify smsSource is correct.
        val smsSource = LiveDataTestUtil.getValue(viewModel.smsSource)
        assertThat(actualSmsSource, isEqualTo(smsSource))

        val newMilkListFromSms = LiveDataTestUtil.getValue(viewModel.newMilkListFromSms)
        assertThat(actualNewMilkListFromSms, isEqualTo(newMilkListFromSms))
    }

    @Test
    fun changeSmsSourceCalled_navigateToSmsSourceSelector() {
        val viewModel = createMilkingViewModel()

        // Call change sms source
        viewModel.changeSmsSource()

        val navigateToSmsSourceSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)
        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))
    }
}