package com.pr656d.cattlenotes.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val prefs: SharedPreferences) {

    companion object {
        const val KEY_USER_LOGGED_IN = "PREF_KEY_USER_LOGGED_IN"
    }
}