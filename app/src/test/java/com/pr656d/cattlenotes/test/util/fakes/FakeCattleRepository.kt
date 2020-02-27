package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository

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

    override fun getAllCattle(): List<Cattle> {
        return fakeAppDatabase.cattleDao().getObservableAll().value ?: emptyList()
    }

    override fun getCattleById(id: Long): Cattle {
        return fakeAppDatabase.cattleDao().getCattle(id)
    }

    override fun deleteCattle(cattle: Cattle) {
        fakeAppDatabase.cattleDao().delete(cattle)
    }

    override fun updateCattle(cattle: Cattle): Int {
        return fakeAppDatabase.cattleDao().update(cattle)
    }
}