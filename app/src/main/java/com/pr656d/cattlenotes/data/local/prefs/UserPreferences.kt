package com.pr656d.cattlenotes.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val prefs: SharedPreferences)
    : CattleNotesSharedPreferences {

    companion object {
        const val KEY_USER_LOGGED_IN = "prefs-key-user-logged-in"
    }

    fun getUserLoggedIn(): Boolean = prefs.getBoolean(KEY_USER_LOGGED_IN, false)

    fun setUserLoggedIn(value: Boolean) = prefs.edit().putBoolean(KEY_USER_LOGGED_IN, value).apply()
}