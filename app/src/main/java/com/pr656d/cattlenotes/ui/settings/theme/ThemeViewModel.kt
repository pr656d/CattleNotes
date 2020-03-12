package com.pr656d.cattlenotes.ui.settings.theme

import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * Thin ViewModel for themed Activities that don't have another ViewModel to use with
 * [ThemedActivityDelegate].
 */
class ThemeViewModel @Inject constructor(
    themedActivityDelegate: ThemedActivityDelegate
) : ViewModel(), ThemedActivityDelegate by themedActivityDelegate
