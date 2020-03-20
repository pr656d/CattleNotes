package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import com.pr656d.model.BreedingCycle
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
    override fun addBreeding(breedingCycle: BreedingCycle) {
        return fakeAppDatabase.breedingDao().insert(breedingCycle)
    }

    override fun addAllBreeding(breedingCycleList: List<BreedingCycle>) {
        return fakeAppDatabase.breedingDao().insertAll(breedingCycleList)
    }

    override fun getObservableAllBreeding(): LiveData<List<BreedingCycle>> {
        return fakeAppDatabase.breedingDao().getObservableAll()
    }

    override fun getBreedingById(cattleId: Long): BreedingCycle? {
        return fakeAppDatabase.breedingDao().getByCattleId(cattleId)
    }

    override fun deleteBreeding(breedingCycle: BreedingCycle) {
        return fakeAppDatabase.breedingDao().delete(breedingCycle)
    }

    override fun updateBreeding(breedingCycle: BreedingCycle): Int {
        return fakeAppDatabase.breedingDao().update(breedingCycle)
    }
}