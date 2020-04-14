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

    override fun getAllCattle(): LiveData<List<Cattle>> {
        return fakeAppDatabase.cattleDao().getAll()
    }

    override fun getCattleById(id: String): LiveData<Cattle?> {
        return fakeAppDatabase.cattleDao().getById(id)
    }

    override fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?> {
        return fakeAppDatabase.cattleDao().getByTagNumber(tagNumber)
    }

    override fun deleteCattle(cattle: Cattle) {
        return fakeAppDatabase.cattleDao().delete(cattle)
    }

    override fun updateCattle(cattle: Cattle) {
        return fakeAppDatabase.cattleDao().update(cattle)
    }

    override fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
        return fakeAppDatabase.cattleDao().getRowCountWithMatchingTagNumber(tagNumber) > 0
    }
}