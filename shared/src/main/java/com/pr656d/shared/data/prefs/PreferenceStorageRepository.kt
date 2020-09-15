/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.data.prefs

import android.os.Build
import com.pr656d.model.Milk
import com.pr656d.model.Theme
import com.pr656d.model.themeFromStorageKey
import com.pr656d.shared.data.prefs.datasource.PreferenceStorage
import com.pr656d.shared.utils.toLocalTime
import com.pr656d.shared.utils.toLong
import com.pr656d.shared.utils.toMilkSmsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    fun getObservableLoginCompleted(): Flow<Boolean>

    /**
     * Get [PreferenceStorage.firstTimeProfileSetupCompleted].
     */
    fun getFirstTimeProfileSetupCompleted(): Boolean

    /**
     * Set [PreferenceStorage.firstTimeProfileSetupCompleted].
     */
    fun setFirstTimeProfileSetupCompleted(firstTimeProfileSetupCompleted: Boolean)

    /**
     * Get whether login and all steps completed or not.
     */
    fun getLoginAndAllStepsCompleted(): Boolean {
        return getLoginCompleted() && getFirstTimeProfileSetupCompleted()
    }

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
    fun getObservableSelectedTheme(): Flow<Theme>

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
    fun getObservableReloadData(): Flow<Boolean>

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
    fun getObservablePreferredTimeOfBreedingReminder(): Flow<LocalTime>

    /**
     * Get [PreferenceStorage.preferredMilkSmsSource].
     */
    fun getPreferredMilkSmsSource(): Milk.Source.Sms?

    /**
     * Set [PreferenceStorage.preferredMilkSmsSource].
     */
    fun setPreferredMilkSmsSource(selectedMilkSmsSource: Milk.Source.Sms?)

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
     */
    fun clear()
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

    override fun getObservableLoginCompleted(): Flow<Boolean> {
        return preferenceStorage.observableLoginCompleted
    }

    override fun getFirstTimeProfileSetupCompleted(): Boolean {
        return preferenceStorage.firstTimeProfileSetupCompleted
    }

    override fun setFirstTimeProfileSetupCompleted(firstTimeProfileSetupCompleted: Boolean) {
        preferenceStorage.firstTimeProfileSetupCompleted = firstTimeProfileSetupCompleted
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

    override fun getObservableSelectedTheme(): Flow<Theme> {
        return preferenceStorage.observableSelectedTheme.map {
            it?.let { themeFromStorageKey(it) }
                ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Theme.SYSTEM
                } else {
                    Theme.BATTERY_SAVER
                }
        }
    }

    override fun getReloadData(): Boolean {
        return preferenceStorage.reloadData
    }

    override fun setReloadData(reloadData: Boolean) {
        preferenceStorage.reloadData = reloadData
    }

    override fun getObservableReloadData(): Flow<Boolean> {
        return preferenceStorage.observableReloadData
    }

    override fun getPreferredTimeOfBreedingReminder(): LocalTime {
        return preferenceStorage.preferredTimeOfBreedingReminder.toLocalTime()
    }

    override fun setPreferredTimeOfBreedingReminder(preferredTimeOfBreedingReminder: LocalTime) {
        preferenceStorage.preferredTimeOfBreedingReminder = preferredTimeOfBreedingReminder.toLong()
    }

    override fun getObservablePreferredTimeOfBreedingReminder(): Flow<LocalTime> {
        return preferenceStorage.observablePreferredTimeOfBreedingReminder.map {
            it.toLocalTime()
        }
    }

    override fun getPreferredMilkSmsSource(): Milk.Source.Sms? {
        return preferenceStorage.preferredMilkSmsSource?.toMilkSmsSource()
    }

    override fun setPreferredMilkSmsSource(selectedMilkSmsSource: Milk.Source.Sms?) {
        preferenceStorage.preferredMilkSmsSource = selectedMilkSmsSource?.SENDER_ADDRESS
    }

    override fun getAutomaticMilkingCollection(): Boolean {
        return preferenceStorage.automaticMilkingCollection
    }

    override fun setAutomaticMilkingCollection(enabled: Boolean) {
        preferenceStorage.automaticMilkingCollection = enabled
    }

    override fun clear() {
        preferenceStorage.clear()
    }
}
