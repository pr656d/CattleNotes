package com.pr656d.shared.data.db.updater

import com.pr656d.shared.data.breeding.BreedingUpdater
import com.pr656d.shared.data.cattle.CattleUpdater
import javax.inject.Inject

/**
 * Syncs remote database with local database.
 */
interface DbUpdater {
    /** Start db updater */
    fun initialize()

    /** Stop db updater */
    fun stop()
}

class DatabaseUpdater @Inject constructor(
    private val cattleUpdater: CattleUpdater,
    private val breedingUpdater: BreedingUpdater
) : DbUpdater {
    override fun initialize() {
        cattleUpdater.initialize()
        breedingUpdater.initialize()
    }

    override fun stop() {
        cattleUpdater.stop()
        breedingUpdater.stop()
    }
}