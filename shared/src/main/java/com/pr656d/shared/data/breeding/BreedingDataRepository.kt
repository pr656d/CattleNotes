package com.pr656d.shared.data.breeding

import androidx.lifecycle.LiveData
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Breeding] data for the presentation layer.
 */
interface BreedingRepository {
    fun addBreeding(breeding: Breeding)

    fun addAllBreeding(breedingList: List<Breeding>)

    fun getObservableAllBreeding(): LiveData<List<Breeding>>

    fun getAllBreedingWithCattle(): List<BreedingWithCattle>

    fun getAllBreedingByCattleId(cattleId: Long): List<Breeding>

    fun deleteBreeding(breeding: Breeding)

    fun updateBreeding(breeding: Breeding): Int
}

@Singleton
open class BreedingDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) : BreedingRepository {
    override fun addBreeding(breeding: Breeding) =
        appDatabase.breedingDao().insert(breeding)

    override fun addAllBreeding(breedingList: List<Breeding>) =
        appDatabase.breedingDao().insertAll(breedingList)

    override fun getObservableAllBreeding(): LiveData<List<Breeding>> =
        appDatabase.breedingDao().getObservableAll()

    override fun getAllBreedingWithCattle(): List<BreedingWithCattle> =
        appDatabase.breedingDao().getAllBreedingCycleWithCattle()

    override fun getAllBreedingByCattleId(cattleId: Long): List<Breeding> =
        appDatabase.breedingDao().getAllByCattleId(cattleId)

    override fun deleteBreeding(breeding: Breeding) =
        appDatabase.breedingDao().delete(breeding)

    override fun updateBreeding(breeding: Breeding): Int =
        appDatabase.breedingDao().update(breeding)
}