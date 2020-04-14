package com.pr656d.shared.data.db.updater

import com.pr656d.shared.data.breeding.datasources.BreedingDataSource
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import javax.inject.Inject

/**
 * Load data from data source and save to Local db.
 */
interface DbLoader {
    /**
     * Load data.
     */
    fun load()

    /**
     * Name convenience wrapper over load.
     */
    fun reload()
}

class DatabaseLoader @Inject constructor(
    private val cattleDataSource: CattleDataSource,
    private val breedingDataSource: BreedingDataSource
) : DbLoader {

    override fun load() {
        /**
         * First load cattle data
         */
        cattleDataSource.load(onComplete = {
            /**
             * Then load breeding data. Breeding data has foreign key to cattle data.
             * Wait for cattle data to be completed.
             */
            breedingDataSource.load()
        })
    }

    override fun reload() {
        load()
    }
}