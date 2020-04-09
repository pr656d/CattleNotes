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
 */
interface BreedingRepository {
    /** @param [saveToLocal] use when this function is called by data source */
    fun addBreeding(breeding: Breeding, saveToLocal: Boolean = false)

    fun getObservableAllBreeding(): LiveData<List<Breeding>>

    fun getAllBreedingWithCattle(): List<BreedingWithCattle>

    fun getAllBreedingByCattleId(cattleId: String): List<Breeding>

    /** @param [saveToLocal] use when this function is called by data source */
    fun deleteBreeding(breeding: Breeding, saveToLocal: Boolean = false)

    /** @param [saveToLocal] use when this function is called by data source */
    fun updateBreeding(breeding: Breeding, saveToLocal: Boolean = false)
}

@Singleton
open class BreedingDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val breedingDataSource: BreedingDataSource
) : BreedingRepository {
    override fun addBreeding(breeding: Breeding, saveToLocal: Boolean) {
        if (saveToLocal)
            appDatabase.breedingDao().insert(breeding)
        else
            breedingDataSource.addBreeding(breeding)
    }

    override fun getObservableAllBreeding(): LiveData<List<Breeding>> =
        appDatabase.breedingDao().getObservableAll()

    override fun getAllBreedingWithCattle(): List<BreedingWithCattle> =
        appDatabase.breedingDao().getAllBreedingCycleWithCattle()

    override fun getAllBreedingByCattleId(cattleId: String): List<Breeding> =
        appDatabase.breedingDao().getAllByCattleId(cattleId)

    override fun deleteBreeding(breeding: Breeding, saveToLocal: Boolean) {
        if (saveToLocal)
            appDatabase.breedingDao().delete(breeding)
        else
            breedingDataSource.deleteBreeding(breeding)
    }

    override fun updateBreeding(breeding: Breeding, saveToLocal: Boolean) {
        if (saveToLocal)
            appDatabase.breedingDao().update(breeding)
        else
            breedingDataSource.updateBreeding(breeding)
    }
}