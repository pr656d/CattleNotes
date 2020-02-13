package com.pr656d.cattlenotes.data.repository

import androidx.lifecycle.LiveData
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.model.BreedingCycle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class BreedingDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    suspend fun addBreeding(breedingCycle: BreedingCycle) =
        appDatabase.breedingDao().insert(breedingCycle)

    suspend fun addAllBreeding(breedingCycle: List<BreedingCycle>) =
        appDatabase.breedingDao().insertAll(breedingCycle)

    fun getAllBreedingCycle(): LiveData<List<BreedingCycle>> = appDatabase.breedingDao().getAll()

    suspend fun getBreedingCycleByTagNumber(cattleId: Long): BreedingCycle? =
        appDatabase.breedingDao().getBreedingCycleByCattleId(cattleId)

    suspend fun deleteBreeding(breedingCycle: BreedingCycle) =
        appDatabase.breedingDao().delete(breedingCycle)

    suspend fun updateBreeding(breedingCycle: BreedingCycle): Int =
        appDatabase.breedingDao().update(breedingCycle)
}