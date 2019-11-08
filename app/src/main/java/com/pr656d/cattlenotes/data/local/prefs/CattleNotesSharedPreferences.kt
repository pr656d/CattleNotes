package com.pr656d.cattlenotes.data.local.prefs

/**
 * Interface to be implemented by all classes whoever is used for SharedPreferences.
 */
interface CattleNotesSharedPreferences {
    companion object {
        const val preferencesName = "cattlenotes-prefs"
    }
}