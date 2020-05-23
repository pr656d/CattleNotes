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

package com.pr656d.cattlenotes.test.fakes.data.prefs

import com.pr656d.model.Milk
import com.pr656d.model.Theme
import com.pr656d.model.themeFromStorageKey
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.prefs.datasource.PreferenceStorage
import com.pr656d.shared.utils.toLocalTime
import com.pr656d.shared.utils.toMilkSmsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mockito.Mockito
import org.threeten.bp.LocalTime

open class FakePreferenceStorageRepository(
    private val preferenceStorage: PreferenceStorage =
        Mockito.mock(PreferenceStorage::class.java)
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

    override fun getObservableLoginCompleted(): Flow<Boolean> {
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

    override fun getObservableSelectedTheme(): Flow<Theme> {
        return preferenceStorage.observableSelectedTheme.map {
            it?.let { themeFromStorageKey(it) } ?: Theme.SYSTEM
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

    override fun getObservableReloadData(): Flow<Boolean> {
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

    override fun getObservablePreferredTimeOfBreedingReminder(): Flow<LocalTime> {
        return preferenceStorage.observablePreferredTimeOfBreedingReminder.map {
            it.toLocalTime()
        }
    }

    override fun getPreferredMilkSmsSource(): Milk.Source.Sms? {
        return preferenceStorage.preferredMilkSmsSource?.toMilkSmsSource()
    }

    override fun setPreferredMilkSmsSource(selectedMilkSmsSource: Milk.Source.Sms?) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun getAutomaticMilkingCollection(): Boolean {
        return preferenceStorage.automaticMilkingCollection
    }

    override fun setAutomaticMilkingCollection(enabled: Boolean) {
        /**
         * Do nothing [PreferenceStorage] does not have getter setter functions.
         * It has variables which setter can not be mocked.
         */
    }

    override fun clear() { }
}