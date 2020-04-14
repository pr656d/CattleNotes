package com.pr656d.shared.data.breeding

import androidx.lifecycle.LiveData
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.datasources.BreedingDataSource
import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Breeding] data for the presentation layer.
 *
 * Info: Data will be loaded by data source. CRUD operations will be done on
 * Local DB as well as at data source to optimise operation count.
 */
interface BreedingRepository {
    fun addBreeding(breeding: Breeding)

    fun getAllBreeding(): LiveData<List<Breeding>>

    fun getAllBreedingWithCattle(): LiveData<List<BreedingWithCattle>>

    fun getAllBreedingByCattleId(cattleId: String): LiveData<List<Breeding>>

    fun deleteBreeding(breeding: Breeding)

    fun updateBreeding(breeding: Breeding)
}

@Singleton
open class BreedingDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val breedingDataSource: BreedingDataSource
) : BreedingRepository {

    /**
     * Add breeding at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun addBreeding(breeding: Breeding) {
        appDatabase.breedingDao().insert(breeding)
        breedingDataSource.addBreeding(breeding)
    }

    override fun getAllBreeding(): LiveData<List<Breeding>> {
        return appDatabase.breedingDao().getAll()
    }

    override fun getAllBreedingWithCattle(): LiveData<List<BreedingWithCattle>> {
        return appDatabase.breedingDao().getAllBreedingCycleWithCattle()
    }

    override fun getAllBreedingByCattleId(cattleId: String): LiveData<List<Breeding>> {
        return appDatabase.breedingDao().getAllByCattleId(cattleId)
    }

    /**
     * Delete breeding at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun deleteBreeding(breeding: Breeding) {
        appDatabase.breedingDao().delete(breeding)
        breedingDataSource.deleteBreeding(breeding)
    }

    /**
     * Update breeding at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun updateBreeding(breeding: Breeding) {
        appDatabase.breedingDao().update(breeding)
        breedingDataSource.updateBreeding(breeding)
    }
}