package com.pr656d.shared.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.model.Theme
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var loginCompleted: Boolean
    var onboardingCompleted: Boolean
    var selectedTheme: String?
    var observableSelectedTheme: LiveData<String>
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

    private val observableSelectedThemeResult = MutableLiveData<String>()

    private val changeListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_DARK_MODE_ENABLED -> observableSelectedThemeResult.value = selectedTheme
        }
    }

    override var loginCompleted by BooleanPreference(
        prefs,
        PREF_LOG_IN,
        false
    )

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

    companion object {
        const val PREFS_NAME = "cattlenotes"
        const val PREF_LOG_IN = "pref_logged_in"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_DARK_MODE_ENABLED = "pref_dark_mode"
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