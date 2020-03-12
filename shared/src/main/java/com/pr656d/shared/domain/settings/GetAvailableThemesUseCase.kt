package com.pr656d.shared.domain.settings

import android.os.Build
import com.pr656d.model.Theme
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.domain.internal.SyncScheduler
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