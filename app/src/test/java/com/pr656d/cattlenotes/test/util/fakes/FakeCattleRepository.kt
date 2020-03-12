package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository

/**
 * Fake of [CattleRepository].
 *
 * Whoever needs this will override methods with desired output needed.
 * This will allow us not to implement unnecessary methods every time we create it.
 *
 * Example:
 *      val fakeCattleRepository = object : FakeCattleRepository {
 *             // Override whatever method you want to use.
 *      }
 */
open class FakeCattleRepository(
    private val fakeAppDatabase: FakeAppDatabase = FakeAppDatabase()
) : CattleRepository {
    override fun addCattle(cattle: Cattle) {
        fakeAppDatabase.cattleDao().insert(cattle)
    }

    override fun addAllCattle(cattleList: List<Cattle>) {
        fakeAppDatabase.cattleDao().insertAll(cattleList)
    }

    override fun getObservableAllCattle(): LiveData<List<Cattle>> {
        return fakeAppDatabase.cattleDao().getObservableAll()
    }

    override fun getCattleById(id: Long): Cattle? {
        return fakeAppDatabase.cattleDao().getCattleById(id)
    }

    override fun getCattleByTagNumber(tagNumber: Long): Cattle? {
        return fakeAppDatabase.cattleDao().getCattleByTagNumber(tagNumber)
    }

    override fun deleteCattle(cattle: Cattle) {
        fakeAppDatabase.cattleDao().delete(cattle)
    }

    override fun updateCattle(cattle: Cattle): Int {
        return fakeAppDatabase.cattleDao().update(cattle)
    }
}