package com.pr656d.cattlenotes.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val prefs: SharedPreferences)
    : CattleNotesSharedPreferences {

    companion object {
        const val KEY_USER_LOGGED_IN = "prefs_key_user_logged_in"
        const val KEY_USER_FIRST_APP_LAUNCH = "prefs_key_user_first_launch"
    }

    fun getUserLoggedIn(): Boolean = prefs.getBoolean(KEY_USER_LOGGED_IN, false)

    fun setUserLoggedIn(value: Boolean) = prefs.edit().putBoolean(KEY_USER_LOGGED_IN, value).apply()

    fun getUserFirstLaunch(): Boolean = prefs.getBoolean(KEY_USER_FIRST_APP_LAUNCH, false)

    fun setUserFirstLaunch(value: Boolean) = prefs.edit().putBoolean(KEY_USER_LOGGED_IN, value).apply()
}