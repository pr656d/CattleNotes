package com.pr656d.cattlenotes.test.util.fakes

import androidx.lifecycle.LiveData
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms

open class FakeMilkRepository(
    private val fakeAppDatabase: FakeAppDatabase = FakeAppDatabase(),
    private val fakeMilkDataSourceFromSms: MilkDataSourceFromSms = FakeMilkDataSourceFromSms()
) : MilkRepository {
    override fun addMilk(milk: Milk) {
        fakeAppDatabase.milkDao().insert(milk)
    }

    override fun addAllMilk(milkList: List<Milk>): List<Long> {
        return fakeAppDatabase.milkDao().insertAll(milkList)
    }

    override fun getAllMilk(): LiveData<List<Milk>> {
        return fakeAppDatabase.milkDao().getAll()
    }

    override fun getAllMilkUnobserved(): List<Milk> {
        return fakeAppDatabase.milkDao().getAllUnobserved()
    }

    override fun getMilkById(id: String): LiveData<Milk?> {
        return fakeAppDatabase.milkDao().getById(id)
    }

    override fun deleteMilk(milk: Milk) {
        fakeAppDatabase.milkDao().delete(milk)
    }

    override fun updateMilk(milk: Milk) {
        fakeAppDatabase.milkDao().update(milk)
    }

    override fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> {
        return fakeMilkDataSourceFromSms.getAllMilk(smsSource)
    }
}