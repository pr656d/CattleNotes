package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.pr656d.model.Milk
import com.pr656d.model.Theme
import com.pr656d.model.themeFromStorageKey
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.prefs.datasource.PreferenceStorage
import com.pr656d.shared.utils.toLocalTime
import com.pr656d.shared.utils.toMilkSmsSource
import org.mockito.Mockito
import org.threeten.bp.LocalTime

open class FakePreferenceStorageRepository(
    private val preferenceStorage: PreferenceStorage = Mockito.mock(PreferenceStorage::class.java)
) : PreferenceStorageRepository {
    override fun getLoginCompleted(): Boolean {
        return preferenceStorage.loginCompleted
    }

    override fun setLoginCompleted(loginCompleted: Boolean) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
        preferenceStorage.loginCompleted = loginCompleted
    }

    override fun getObservableLoginCompleted(): LiveData<Boolean> {
        return preferenceStorage.observableLoginCompleted
    }

    override fun getFirstTimeProfileSetupCompleted(): Boolean {
        return preferenceStorage.firstTimeProfileSetupCompleted
    }

    override fun setFirstTimeProfileSetupCompleted(firstTimeProfileSetupCompleted: Boolean) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun getLoginAndAllStepsCompleted(): Boolean {
        return preferenceStorage.loginAndAllStepsCompleted
    }

    override fun getOnboardingCompleted(): Boolean {
        return preferenceStorage.onboardingCompleted
    }

    override fun setOnboardingCompleted(onboardingCompleted: Boolean) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun getSelectedTheme(): Theme? {
        return preferenceStorage.selectedTheme?.let {
            themeFromStorageKey(it)
        }
    }

    override fun setSelectedTheme(selectedTheme: Theme?) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun getObservableSelectedTheme(): LiveData<Theme> {
        return preferenceStorage.observableSelectedTheme.map {
            themeFromStorageKey(it)
        }
    }

    override fun getReloadData(): Boolean {
        return preferenceStorage.reloadData
    }

    override fun setReloadData(reloadData: Boolean) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun getObservableReloadData(): LiveData<Boolean> {
        return preferenceStorage.observableReloadData
    }

    override fun getPreferredTimeOfBreedingReminder(): LocalTime {
        return preferenceStorage.preferredTimeOfBreedingReminder.toLocalTime()
    }

    override fun setPreferredTimeOfBreedingReminder(preferredTimeOfBreedingReminder: LocalTime) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun getObservablePreferredTimeOfBreedingReminder(): LiveData<LocalTime> {
        return preferenceStorage.observablePreferredTimeOfBreedingReminder.map {
            it.toLocalTime()
        }
    }

    override fun getSelectedMilkSmsSource(): Milk.Source.Sms? {
        return preferenceStorage.selectedMilkSmsSource?.toMilkSmsSource()
    }

    override fun setSelectedMilkSmsSource(selectedMilkSmsSource: Milk.Source.Sms?) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun clear(fromRemote: Boolean) {
        /**
         * We don't have to do anything.
         */
    }
}