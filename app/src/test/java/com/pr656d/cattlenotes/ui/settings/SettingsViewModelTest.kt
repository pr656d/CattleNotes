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

package com.pr656d.cattlenotes.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.prefs.FakePreferenceStorageRepository
import com.pr656d.model.Theme
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.settings.*
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalTime
import org.hamcrest.Matchers.equalTo as isEqualTo

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private val preferenceStorageRepository =
        object : FakePreferenceStorageRepository() {
            override fun getSelectedTheme(): Theme? = Theme.SYSTEM

            override fun getAutomaticMilkingCollection(): Boolean = true

            override fun getObservablePreferredTimeOfBreedingReminder(): Flow<LocalTime> {
                return flowOf(LocalTime.now())
            }
        }

    private fun createSettingsViewModel(
        fakePreferenceStorageRepository: FakePreferenceStorageRepository = preferenceStorageRepository
    ): SettingsViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return SettingsViewModel(
            GetThemeUseCase(fakePreferenceStorageRepository, coroutineDispatcher),
            SetThemeUseCase(fakePreferenceStorageRepository, coroutineDispatcher),
            GetAvailableThemesUseCase(coroutineDispatcher),
            GetAutomaticMilkingCollectionUseCase(
                fakePreferenceStorageRepository,
                coroutineDispatcher
            ),
            SetAutomaticMilkingCollectionUseCase(
                fakePreferenceStorageRepository,
                coroutineDispatcher
            ),
            ObservePreferredTimeOfBreedingReminderUseCase(
                fakePreferenceStorageRepository,
                coroutineDispatcher
            ),
            GetPreferredMilkSmsSourceUseCase(fakePreferenceStorageRepository, coroutineDispatcher),
            SetPreferredTimeOfBreedingReminderUseCase(
                fakePreferenceStorageRepository,
                coroutineDispatcher
            )
        )
    }

    @Test
    fun onThemeSettingCalled_navigateToThemeSelector() = coroutineRule.runBlockingTest {
        val viewModel = createSettingsViewModel()

        // Call onThemeSettingClicked
        viewModel.onThemeSettingClicked()

        val navigateToThemeSelector = LiveDataTestUtil.getValue(viewModel.navigateToThemeSelector)
        assertThat(Unit, isEqualTo(navigateToThemeSelector?.getContentIfNotHandled()))
    }

    @Test
    fun onBreedingReminderClicked_navigateToBreedingReminderTimeSelector() = coroutineRule.runBlockingTest {
        val viewModel = createSettingsViewModel()

        // Call onBreedingReminderClicked
        viewModel.onBreedingReminderClicked()

        val navigateToBreedingReminderTimeSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToBreedingReminderTimeSelector)

        assertThat(
            Unit,
            isEqualTo(navigateToBreedingReminderTimeSelector?.getContentIfNotHandled())
        )
    }

    @Test
    fun onMilkSmsSenderClicked_navigateToSmsSourceSelector() = coroutineRule.runBlockingTest {
        val viewModel = createSettingsViewModel()

        // Call onMilkSmsSenderClicked
        viewModel.onMilkSmsSenderClicked()

        val navigateToSmsSourceSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)

        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))
    }

    @Test
    fun toggleAutomaticMilkCollectionCalled_setAutomaticMilkingCollection() = coroutineRule.runBlockingTest {
        val viewModel = createSettingsViewModel()

        // Call toggleAutomaticMilkCollection
        viewModel.toggleAutomaticMilkCollection(false)

        val automaticMilkingCollection =
            LiveDataTestUtil.getValue(viewModel.automaticMilkingCollection)

        assertThat(true, isEqualTo(automaticMilkingCollection))
    }

    @Test
    fun creditsClicked_navigateToCredits() = coroutineRule.runBlockingTest {
        val viewModel = createSettingsViewModel()

        // Call creditsClicked
        viewModel.creditsClicked()

        val navigateToCredits = LiveDataTestUtil.getValue(viewModel.navigateToCredits)

        assertThat(Unit, isEqualTo(navigateToCredits?.getContentIfNotHandled()))
    }

    @Test
    fun openSourceLicensesClicked_navigateToOpenSourceLicenses() = coroutineRule.runBlockingTest {
        val viewModel = createSettingsViewModel()

        // Call openSourceLicensesClicked
        viewModel.openSourceLicensesClicked()

        val navigateToOpenSourceLicenses =
            LiveDataTestUtil.getValue(viewModel.navigateToOpenSourceLicenses)

        assertThat(Unit, isEqualTo(navigateToOpenSourceLicenses?.getContentIfNotHandled()))
    }
}