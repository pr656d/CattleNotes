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

package com.pr656d.shared.data.prefs.datasource

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.pr656d.model.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.threeten.bp.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 * @see SharedPreferenceStorage
 */
interface PreferenceStorage {
    var loginCompleted: Boolean

    var observableLoginCompleted: Flow<Boolean>

    var firstTimeProfileSetupCompleted: Boolean

    var onboardingCompleted: Boolean

    var selectedTheme: String?

    var observableSelectedTheme: Flow<String?>

    /**
     * Reload user data from data source.
     */
    var reloadData: Boolean

    var observableReloadData: Flow<Boolean>

    /**
     * Hold preferred time for breeding event reminder.
     * Saves [org.threeten.bp.LocalTime.toNanoOfDay].
     */
    var preferredTimeOfBreedingReminder: Long

    var observablePreferredTimeOfBreedingReminder: Flow<Long>

    var preferredMilkSmsSource: String?

    var automaticMilkingCollection: Boolean

    /**
     * Clear the shared preferences.
     */
    fun clear()
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class SharedPreferenceStorage @Inject constructor(context: Context)
    : PreferenceStorage {

    private val selectedThemeChannel: ConflatedBroadcastChannel<String?> by lazy {
        ConflatedBroadcastChannel<String?>().apply {
            offer(selectedTheme)
        }
    }

    private val loginCompletedChannel: ConflatedBroadcastChannel<Boolean> by lazy {
        ConflatedBroadcastChannel<Boolean>().apply {
            offer(loginCompleted)
        }
    }

    private val reloadDataChannel: ConflatedBroadcastChannel<Boolean> by lazy {
        ConflatedBroadcastChannel<Boolean>().apply {
            offer(reloadData)
        }
    }

    private val preferredTimeOfBreedingReminderChannel: ConflatedBroadcastChannel<Long> by lazy {
        ConflatedBroadcastChannel<Long>().apply {
            offer(preferredTimeOfBreedingReminder)
        }
    }

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val changeListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_DARK_MODE_ENABLED -> selectedThemeChannel.offer(selectedTheme)

            PREF_LOG_IN -> loginCompletedChannel.offer(loginCompleted)

            PREF_RELOAD_DATA -> reloadDataChannel.offer(reloadData)

            PREF_PREFERRED_TIME_OF_BREEDING_REMINDER ->
                preferredTimeOfBreedingReminderChannel.offer(preferredTimeOfBreedingReminder)
        }
    }

    override var loginCompleted by BooleanPreference(
        prefs,
        PREF_LOG_IN,
        false
    )

    override var observableLoginCompleted: Flow<Boolean>
        get() = loginCompletedChannel.asFlow()
        set(_) = throw IllegalAccessException("This property can't be changed")


    override var onboardingCompleted by BooleanPreference(
        prefs,
        PREF_ONBOARDING,
        false
    )

    override var selectedTheme by StringPreference(
        prefs,
        PREF_DARK_MODE_ENABLED,
        Theme.SYSTEM.storageKey
    )

    override var observableSelectedTheme: Flow<String?>
        get() = selectedThemeChannel.asFlow()
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var firstTimeProfileSetupCompleted by BooleanPreference(
        prefs,
        PREF_FIRST_TIME_PROFILE_SETUP,
        false
    )

    override var reloadData by BooleanPreference(
        prefs,
        PREF_RELOAD_DATA,
        false
    )

    override var observableReloadData: Flow<Boolean>
        get() = reloadDataChannel.asFlow()
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var preferredTimeOfBreedingReminder: Long by LongPreference(
        prefs,
        PREF_PREFERRED_TIME_OF_BREEDING_REMINDER,
        DEFAULT_REMINDER_TIME
    )

    override var observablePreferredTimeOfBreedingReminder: Flow<Long>
        get() = preferredTimeOfBreedingReminderChannel.asFlow()
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var preferredMilkSmsSource: String? by StringPreference(
        prefs,
        PREF_MILK_SMS_SOURCE,
        null
    )

    override var automaticMilkingCollection: Boolean by BooleanPreference(
        prefs,
        PREF_AUTOMATIC_MILKING_COLLECTION,
        true
    )

    override fun clear() {
        prefs.value.edit { clear() }
    }

    companion object {
        const val PREFS_NAME = "cattlenotes"
        const val PREF_LOG_IN = "pref_logged_in"
        const val PREF_FIRST_TIME_PROFILE_SETUP = "pref_first_time_profile_setup"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_DARK_MODE_ENABLED = "pref_dark_mode"
        const val PREF_RELOAD_DATA = "reload_data"
        const val PREF_PREFERRED_TIME_OF_BREEDING_REMINDER = "preferred_time_of_breeding_reminder"
        const val PREF_MILK_SMS_SOURCE = "milk_sms_source"
        const val PREF_AUTOMATIC_MILKING_COLLECTION = "automatic_milking_collection"

        // Default time for reminders will be 09:00 AM in nano day.
        val DEFAULT_REMINDER_TIME by lazy { LocalTime.of(9, 0).toNanoOfDay() }
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}

class LongPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit { putLong(name, value) }
    }
}