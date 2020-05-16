package com.pr656d.shared.data.prefs.datasource

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.model.Theme
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

    var observableLoginCompleted: LiveData<Boolean>

    var firstTimeProfileSetupCompleted: Boolean

    /**
     * Login and it's first steps after login are completed.
     *
     * First user experience.
     */
    val loginAndAllStepsCompleted: Boolean

    var onboardingCompleted: Boolean

    var selectedTheme: String?

    var observableSelectedTheme: LiveData<String>

    /**
     * Reload user data from data source.
     */
    var reloadData: Boolean

    var observableReloadData: LiveData<Boolean>

    /**
     * Hold preferred time for breeding event reminder.
     * Saves [org.threeten.bp.LocalTime.toNanoOfDay].
     */
    var preferredTimeOfBreedingReminder: Long

    var observablePreferredTimeOfBreedingReminder: LiveData<Long>

    var selectedMilkSmsSource: String?

    var observableSelectedMilkSmsSource: LiveData<String>

    var automaticMilkingCollection: Boolean

    /**
     * Clear the shared preferences.
     */
    fun clear()
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
@Singleton
class SharedPreferenceStorage @Inject constructor(context: Context)
    : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val observableLoginCompletedResult = MutableLiveData<Boolean>()

    private val observableSelectedThemeResult = MutableLiveData<String>()

    private val observeReloadDataResult = MutableLiveData<Boolean>()

    private val observePreferredTimeOfBreedingReminderResult = MutableLiveData<Long>()

    private val observeSelectedMilkSmsSourceResult = MutableLiveData<String>()

    private val changeListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_DARK_MODE_ENABLED -> observableSelectedThemeResult.value = selectedTheme

            PREF_LOG_IN -> observableLoginCompletedResult.value = loginCompleted

            PREF_RELOAD_DATA -> observeReloadDataResult.value = reloadData

            PREF_PREFERRED_TIME_OF_BREEDING_REMINDER ->
                observePreferredTimeOfBreedingReminderResult.value = preferredTimeOfBreedingReminder

            PREF_MILK_SMS_SOURCE -> observeSelectedMilkSmsSourceResult.value = selectedMilkSmsSource
        }
    }

    override var loginCompleted by BooleanPreference(
        prefs,
        PREF_LOG_IN,
        false
    )

    override var observableLoginCompleted: LiveData<Boolean>
        get() {
            observableLoginCompletedResult.value = loginCompleted
            return observableLoginCompletedResult
        }
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

    override var observableSelectedTheme: LiveData<String>
        get() {
            observableSelectedThemeResult.value = selectedTheme
            return observableSelectedThemeResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var firstTimeProfileSetupCompleted by BooleanPreference(
        prefs,
        PREF_FIRST_TIME_PROFILE_SETUP,
        false
    )

    override val loginAndAllStepsCompleted: Boolean
        get() = loginCompleted && firstTimeProfileSetupCompleted

    override var reloadData by BooleanPreference(
        prefs,
        PREF_RELOAD_DATA,
        false
    )

    override var observableReloadData: LiveData<Boolean>
        get() {
            observeReloadDataResult.value = reloadData
            return observeReloadDataResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var preferredTimeOfBreedingReminder: Long by LongPreference(
        prefs,
        PREF_PREFERRED_TIME_OF_BREEDING_REMINDER,
        DEFAULT_REMINDER_TIME
    )

    override var observablePreferredTimeOfBreedingReminder: LiveData<Long>
        get() {
            observePreferredTimeOfBreedingReminderResult.value = preferredTimeOfBreedingReminder
            return observePreferredTimeOfBreedingReminderResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

    override var selectedMilkSmsSource: String? by StringPreference(
        prefs,
        PREF_MILK_SMS_SOURCE,
        null
    )

    override var observableSelectedMilkSmsSource: LiveData<String>
        get() {
            observeSelectedMilkSmsSourceResult.value = selectedMilkSmsSource
            return observeSelectedMilkSmsSourceResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

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