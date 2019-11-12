package com.pr656d.cattlenotes.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.pr656d.cattlenotes.CattleNotesApplication

/**
 * Interface to be implemented by all classes whoever is used for SharedPreferences.
 */
interface CattleNotesSharedPreferences {
    companion object {
        const val preferencesName = "cattlenotes-prefs"

        fun build(application: CattleNotesApplication): SharedPreferences =
            application.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }
}