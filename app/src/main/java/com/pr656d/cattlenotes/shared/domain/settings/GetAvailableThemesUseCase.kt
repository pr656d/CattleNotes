package com.pr656d.cattlenotes.shared.domain.settings

import android.os.Build
import com.pr656d.cattlenotes.data.model.Theme
import com.pr656d.cattlenotes.shared.domain.UseCase
import com.pr656d.cattlenotes.shared.domain.internal.SyncScheduler
import javax.inject.Inject

class GetAvailableThemesUseCase @Inject constructor() : UseCase<Unit, List<Theme>>() {
    init {
        taskScheduler = SyncScheduler
    }

    override fun execute(parameters: Unit): List<Theme> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM)
        }
        else -> {
            listOf(Theme.LIGHT, Theme.DARK, Theme.BATTERY_SAVER)
        }
    }
}