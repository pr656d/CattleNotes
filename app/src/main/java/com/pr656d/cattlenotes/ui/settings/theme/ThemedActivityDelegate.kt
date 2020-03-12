package com.pr656d.cattlenotes.ui.settings.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.pr656d.cattlenotes.ui.settings.ObserveThemeModeUseCase
import com.pr656d.model.Theme
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.domain.settings.GetThemeUseCase
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Interface to implement activity theming via a ViewModel.
 *
 * You can inject a implementation of this via Dagger2, then use the implementation as an interface
 * delegate to add the functionality without writing any code
 *
 * Example usage:
 * ```
 * class MyViewModel @Inject constructor(
 *     themedActivityDelegate: ThemedActivityDelegate
 * ) : ViewModel(), ThemedActivityDelegate by themedActivityDelegate {
 * ```
 */
interface ThemedActivityDelegate {
    /**
     * Allows observing of the current theme
     */
    val theme: LiveData<Theme>

    /**
     * Allows querying of the current theme synchronously
     */
    val currentTheme: Theme
}

class ThemedActivityDelegateImpl @Inject constructor(
    private val observeThemeUseCase: ObserveThemeModeUseCase,
    private val getThemeUseCase: GetThemeUseCase
) : ThemedActivityDelegate {
    override val theme: LiveData<Theme> by lazy(NONE) {
        observeThemeUseCase.observe().map {
            if (it is Success) it.data else Theme.SYSTEM
        }
    }

    override val currentTheme: Theme
        get() = getThemeUseCase.executeNow(Unit).let {
            if (it is Success) it.data else Theme.SYSTEM
        }

    init {
        // Observe updates in dark mode setting
        observeThemeUseCase.execute(Unit)
    }
}
