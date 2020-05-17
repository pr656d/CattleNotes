package com.pr656d.shared.data.prefs

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.pr656d.model.Milk
import com.pr656d.model.Theme
import com.pr656d.model.themeFromStorageKey
import com.pr656d.shared.data.prefs.datasource.PreferenceStorage
import com.pr656d.shared.utils.toLocalTime
import com.pr656d.shared.utils.toLong
import com.pr656d.shared.utils.toMilkSmsSource
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * Single point of access for [PreferenceStorage] data for the presentation layer.
 * @see SharedPreferenceStorageRepository
 */
interface PreferenceStorageRepository {
    /**
     * Get [PreferenceStorage.loginCompleted].
     */
    fun getLoginCompleted(): Boolean

    /**
     * Set [PreferenceStorage.loginCompleted].
     */
    fun setLoginCompleted(loginCompleted: Boolean)

    /**
     * Get [PreferenceStorage.observableLoginCompleted].
     */
    fun getObservableLoginCompleted(): LiveData<Boolean>

    /**
     * Get [PreferenceStorage.firstTimeProfileSetupCompleted].
     */
    fun getFirstTimeProfileSetupCompleted(): Boolean

    /**
     * Set [PreferenceStorage.firstTimeProfileSetupCompleted].
     */
    fun setFirstTimeProfileSetupCompleted(firstTimeProfileSetupCompleted: Boolean)

    /**
     * Get [PreferenceStorage.loginAndAllStepsCompleted].
     */
    fun getLoginAndAllStepsCompleted(): Boolean

    /**
     * Get [PreferenceStorage.onboardingCompleted].
     */
    fun getOnboardingCompleted(): Boolean

    /**
     * Set [PreferenceStorage.onboardingCompleted].
     */
    fun setOnboardingCompleted(onboardingCompleted: Boolean)

    /**
     * Get [PreferenceStorage.selectedTheme].
     */
    fun getSelectedTheme(): Theme?

    /**
     * Set [PreferenceStorage.selectedTheme].
     */
    fun setSelectedTheme(selectedTheme: Theme?)

    /**
     * Get [PreferenceStorage.observableSelectedTheme].
     */
    fun getObservableSelectedTheme(): LiveData<Theme>

    /**
     * Get [PreferenceStorage.reloadData].
     */
    fun getReloadData(): Boolean

    /**
     * Set [PreferenceStorage.reloadData].
     */
    fun setReloadData(reloadData: Boolean)

    /**
     * Get [PreferenceStorage.observableReloadData].
     */
    fun getObservableReloadData(): LiveData<Boolean>

    /**
     * Get [PreferenceStorage.preferredTimeOfBreedingReminder].
     */
    fun getPreferredTimeOfBreedingReminder(): LocalTime

    /**
     * Set [PreferenceStorage.preferredTimeOfBreedingReminder].
     */
    fun setPreferredTimeOfBreedingReminder(preferredTimeOfBreedingReminder: LocalTime)

    /**
     * Get [PreferenceStorage.observablePreferredTimeOfBreedingReminder].
     */
    fun getObservablePreferredTimeOfBreedingReminder(): LiveData<LocalTime>

    /**
     * Get [PreferenceStorage.preferredMilkSmsSource].
     */
    fun getPreferredMilkSmsSource(): Milk.Source.Sms?

    /**
     * Set [PreferenceStorage.preferredMilkSmsSource].
     */
    fun setPreferredMilkSmsSource(selectedMilkSmsSource: Milk.Source.Sms?)

    /**
     * Get [PreferenceStorage.observablePreferredMilkSmsSource]
     */
    fun getObservablePreferredMilkSmsSource(): LiveData<Milk.Source.Sms>

    /**
     * Get [PreferenceStorage.automaticMilkingCollection].
     */
    fun getAutomaticMilkingCollection(): Boolean

    /**
     * Set [PreferenceStorage.automaticMilkingCollection].
     */
    fun setAutomaticMilkingCollection(enabled: Boolean)

    /**
     * Clear the shared preferences.
     * @param fromRemote Pass true if you want to clear from remote source.
     */
    fun clear(fromRemote: Boolean = false)
}

@Singleton
class SharedPreferenceStorageRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : PreferenceStorageRepository {

    override fun getLoginCompleted(): Boolean {
        return preferenceStorage.loginCompleted
    }

    override fun setLoginCompleted(loginCompleted: Boolean) {
        preferenceStorage.loginCompleted = loginCompleted
    }

    override fun getObservableLoginCompleted(): LiveData<Boolean> {
        return preferenceStorage.observableLoginCompleted
    }

    override fun getFirstTimeProfileSetupCompleted(): Boolean {
        return preferenceStorage.firstTimeProfileSetupCompleted
    }

    override fun setFirstTimeProfileSetupCompleted(firstTimeProfileSetupCompleted: Boolean) {
        preferenceStorage.firstTimeProfileSetupCompleted = firstTimeProfileSetupCompleted
    }

    override fun getLoginAndAllStepsCompleted(): Boolean {
        return preferenceStorage.loginAndAllStepsCompleted
    }

    override fun getOnboardingCompleted(): Boolean {
        return preferenceStorage.onboardingCompleted
    }

    override fun setOnboardingCompleted(onboardingCompleted: Boolean) {
        preferenceStorage.onboardingCompleted = onboardingCompleted
    }

    override fun getSelectedTheme(): Theme? {
        return preferenceStorage.selectedTheme?.let { themeFromStorageKey(it) }
    }

    override fun setSelectedTheme(selectedTheme: Theme?) {
        preferenceStorage.selectedTheme = selectedTheme?.storageKey
    }

    override fun getObservableSelectedTheme(): LiveData<Theme> {
        return Transformations.map(preferenceStorage.observableSelectedTheme) {
            it?.let { themeFromStorageKey(it) }
        }
    }

    override fun getReloadData(): Boolean {
        return preferenceStorage.reloadData
    }

    override fun setReloadData(reloadData: Boolean) {
        preferenceStorage.reloadData = reloadData
    }

    override fun getObservableReloadData(): LiveData<Boolean> {
        return preferenceStorage.observableReloadData
    }

    override fun getPreferredTimeOfBreedingReminder(): LocalTime {
        return preferenceStorage.preferredTimeOfBreedingReminder.toLocalTime()
    }

    override fun setPreferredTimeOfBreedingReminder(preferredTimeOfBreedingReminder: LocalTime) {
        preferenceStorage.preferredTimeOfBreedingReminder = preferredTimeOfBreedingReminder.toLong()
    }

    override fun getObservablePreferredTimeOfBreedingReminder(): LiveData<LocalTime> {
        return Transformations.map(preferenceStorage.observablePreferredTimeOfBreedingReminder) {
            it.toLocalTime()
        }
    }

    override fun getPreferredMilkSmsSource(): Milk.Source.Sms? {
        return preferenceStorage.preferredMilkSmsSource?.toMilkSmsSource()
    }

    override fun setPreferredMilkSmsSource(selectedMilkSmsSource: Milk.Source.Sms?) {
        preferenceStorage.preferredMilkSmsSource = selectedMilkSmsSource?.SENDER_ADDRESS
    }

    override fun getObservablePreferredMilkSmsSource(): LiveData<Milk.Source.Sms> {
        return Transformations.map(preferenceStorage.observablePreferredMilkSmsSource) {
            it?.toMilkSmsSource()
        }
    }

    override fun getAutomaticMilkingCollection(): Boolean {
        return preferenceStorage.automaticMilkingCollection
    }

    override fun setAutomaticMilkingCollection(enabled: Boolean) {
        preferenceStorage.automaticMilkingCollection = enabled
    }

    override fun clear(fromRemote: Boolean) {
        preferenceStorage.clear()
        if (fromRemote) {
            // TODO("Clear from remote source also.") : If remote source available and asked explicitly to clear from remote source.
        }
    }
}