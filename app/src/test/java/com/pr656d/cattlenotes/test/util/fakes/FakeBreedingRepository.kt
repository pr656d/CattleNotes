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

    override fun getBreeding(breedingId: String): LiveData<Breeding?> {
        return fakeAppDatabase.breedingDao().get(breedingId)
    }

    override fun getAllBreeding(): LiveData<List<Breeding>> {
        return fakeAppDatabase.breedingDao().getAll()
    }

    override fun getAllBreedingByCattleId(cattleId: String): LiveData<List<Breeding>> {
        return fakeAppDatabase.breedingDao().getAllByCattleId(cattleId)
    }

    override fun getBreedingWithCattle(breedingId: String): LiveData<BreedingWithCattle?> {
        return fakeAppDatabase.breedingDao().getBreedingWithCattle(breedingId)
    }

    override fun getAllBreedingWithCattle(): LiveData<List<BreedingWithCattle>> {
        return fakeAppDatabase.breedingDao().getAllBreedingWithCattle()
    }

    override fun getAllBreedingWithCattleByCattleId(cattleId: String): LiveData<List<BreedingWithCattle>> {
        return fakeAppDatabase.breedingDao().getAllBreedingWithCattleByCattleId(cattleId)
    }

    override fun deleteBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().delete(breeding)
    }

    override fun updateBreeding(breeding: Breeding) {
        return fakeAppDatabase.breedingDao().update(breeding)
    }
}