package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.BreedingRepository

/**
 * Fake of [BreedingRepository].
 *
 * Whoever needs this will override methods with desired output needed.
 * This will allow us not to implement unnecessary methods every time we create it.
 *
 * Example:
 *      val fakeBreedingRepository = object : FakeBreedingRepository {
 *             // Override whatever method you want to use.
 *      }
 */
open class FakeBreedingRepository(
    private val fakeAppDatabase: FakeAppDatabase = FakeAppDatabase()
) : BreedingRepository {
    override fun addBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().insert(breeding)
    }

    override fun getAllBreeding(): LiveData<List<Breeding>> {
        return fakeAppDatabase.breedingDao().getAll()
    }

    override fun getAllBreedingWithCattle(): LiveData<List<BreedingWithCattle>> {
        return fakeAppDatabase.breedingDao().getAllBreedingCycleWithCattle()
    }

    override fun getAllBreedingByCattleId(cattleId: String): LiveData<List<Breeding>> {
        return fakeAppDatabase.breedingDao().getAllByCattleId(cattleId)
    }

    override fun deleteBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().delete(breeding)
    }

    override fun updateBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().update(breeding)
    }
}