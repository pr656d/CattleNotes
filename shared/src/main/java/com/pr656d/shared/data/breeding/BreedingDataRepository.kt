package com.pr656d.shared.data.breeding

import androidx.lifecycle.LiveData
import com.pr656d.model.BreedingCycle
import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [BreedingCycle] data for the presentation layer.
 */
interface BreedingRepository {
    fun addBreeding(breedingCycle: BreedingCycle)
    fun addAllBreeding(breedingCycleList: List<BreedingCycle>)
    fun getObservableAllBreeding(): LiveData<List<BreedingCycle>>
    fun getBreedingById(cattleId: Long): BreedingCycle?
    fun deleteBreeding(breedingCycle: BreedingCycle)
    fun updateBreeding(breedingCycle: BreedingCycle): Int
}

@Singleton
open class BreedingDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) : BreedingRepository {
    override fun addBreeding(breedingCycle: BreedingCycle) =
        appDatabase.breedingDao().insert(breedingCycle)

    override fun addAllBreeding(breedingCycleList: List<BreedingCycle>) =
        appDatabase.breedingDao().insertAll(breedingCycleList)

    override fun getObservableAllBreeding(): LiveData<List<BreedingCycle>> =
        appDatabase.breedingDao().getObservableAll()

    override fun getBreedingById(cattleId: Long): BreedingCycle? =
        appDatabase.breedingDao().getByCattleId(cattleId)

    override fun deleteBreeding(breedingCycle: BreedingCycle) =
        appDatabase.breedingDao().delete(breedingCycle)

    override fun updateBreeding(breedingCycle: BreedingCycle): Int =
        appDatabase.breedingDao().update(breedingCycle)
}