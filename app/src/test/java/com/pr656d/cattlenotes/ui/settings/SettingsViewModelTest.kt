package com.pr656d.cattlenotes.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakePreferenceStorageRepository
import com.pr656d.model.Milk
import com.pr656d.model.Theme
import com.pr656d.shared.domain.milk.sms.ObserveMilkSmsSourceUseCase
import com.pr656d.shared.domain.settings.*
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalTime
import org.hamcrest.Matchers.equalTo as isEqualTo

class SettingsViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private val preferenceStorageRepository =
        object : FakePreferenceStorageRepository() {
            override fun getSelectedTheme(): Theme? = Theme.SYSTEM

            override fun getAutomaticMilkingCollection(): Boolean = true

            override fun getObservablePreferredTimeOfBreedingReminder(): LiveData<LocalTime> {
                return MutableLiveData(LocalTime.now())
            }

            override fun getObservablePreferredMilkSmsSource(): LiveData<Milk.Source.Sms> {
                return MutableLiveData(Milk.Source.Sms.BGAMAMCS)
            }
        }

    private fun createSettingsViewModel(
        fakePreferenceStorageRepository: FakePreferenceStorageRepository = preferenceStorageRepository
    ): SettingsViewModel {
        return SettingsViewModel(
            GetThemeUseCase(fakePreferenceStorageRepository),
            GetAvailableThemesUseCase(),
            GetAutomaticMilkingCollectionUseCase(fakePreferenceStorageRepository),
            ObservePreferredTimeOfBreedingReminderUseCase(fakePreferenceStorageRepository),
            ObserveMilkSmsSourceUseCase(fakePreferenceStorageRepository),
            SetThemeUseCase(fakePreferenceStorageRepository),
            SetPreferredTimeOfBreedingReminderUseCase(fakePreferenceStorageRepository),
            SetAutomaticMilkingCollectionUseCase(fakePreferenceStorageRepository)
        )
    }

    @Test
    fun onThemeSettingCalled_navigateToThemeSelector() {
        val viewModel = createSettingsViewModel()

        // Call onThemeSettingClicked
        viewModel.onThemeSettingClicked()

        val navigateToThemeSelector = LiveDataTestUtil.getValue(viewModel.navigateToThemeSelector)
        assertThat(Unit, isEqualTo(navigateToThemeSelector?.getContentIfNotHandled()))
    }

    @Test
    fun onBreedingReminderClicked_navigateToBreedingReminderTimeSelector() {
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
    fun onMilkSmsSenderClicked_navigateToSmsSourceSelector() {
        val viewModel = createSettingsViewModel()

        // Call onMilkSmsSenderClicked
        viewModel.onMilkSmsSenderClicked()

        val navigateToSmsSourceSelector =
            LiveDataTestUtil.getValue(viewModel.navigateToSmsSourceSelector)

        assertThat(Unit, isEqualTo(navigateToSmsSourceSelector?.getContentIfNotHandled()))
    }

    @Test
    fun toggleAutomaticMilkCollectionCalled_setAutomaticMilkingCollection() {
        val viewModel = createSettingsViewModel()

        // Call toggleAutomaticMilkCollection
        viewModel.toggleAutomaticMilkCollection(false)

        val automaticMilkingCollection =
            LiveDataTestUtil.getValue(viewModel.automaticMilkingCollection)

        assertThat(true, isEqualTo(automaticMilkingCollection))
    }

    @Test
    fun creditsClicked_navigateToCredits() {
        val viewModel = createSettingsViewModel()

        // Call creditsClicked
        viewModel.creditsClicked()

        val navigateToCredits = LiveDataTestUtil.getValue(viewModel.navigateToCredits)

        assertThat(Unit, isEqualTo(navigateToCredits?.getContentIfNotHandled()))
    }

    @Test
    fun openSourceLicensesClicked_navigateToOpenSourceLicenses() {
        val viewModel = createSettingsViewModel()

        // Call openSourceLicensesClicked
        viewModel.openSourceLicensesClicked()

        val navigateToOpenSourceLicenses = LiveDataTestUtil.getValue(viewModel.navigateToOpenSourceLicenses)

        assertThat(Unit, isEqualTo(navigateToOpenSourceLicenses?.getContentIfNotHandled()))
    }
}