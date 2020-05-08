package com.pr656d.shared.data.milk

import androidx.lifecycle.LiveData
import com.pr656d.model.Milk
import com.pr656d.shared.data.db.MilkDao
import com.pr656d.shared.data.milk.datasource.MilkDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import javax.inject.Inject

/**
 *
 * Single point of access for [Milk] data for the presentation layer.
 */
interface MilkRepository {
    fun addMilk(milk: Milk)

    fun getAllMilk(): LiveData<List<Milk>>

    fun getAllMilkUnobserved(): List<Milk>

    fun getMilkById(id: String): LiveData<Milk?>

    fun deleteMilk(milk: Milk)

    fun updateMilk(milk: Milk)

    fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk>
}

class MilkDataRepository @Inject constructor(
    private val milkDao: MilkDao,
    private val milkDataSource: MilkDataSource,
    private val milkDataSourceFromSms: MilkDataSourceFromSms
) : MilkRepository {
    override fun addMilk(milk: Milk) {
        milkDao.insert(milk)
        milkDataSource.addMilk(milk)
    }

    override fun getAllMilk(): LiveData<List<Milk>> {
        return milkDao.getAll()
    }

    override fun getAllMilkUnobserved(): List<Milk> {
        return milkDao.getAllUnobserved()
    }

    override fun getMilkById(id: String): LiveData<Milk?> {
        return milkDao.getById(id)
    }

    override fun deleteMilk(milk: Milk) {
        milkDao.delete(milk)
        milkDataSource.deleteMilk(milk)
    }

    override fun updateMilk(milk: Milk) {
        milkDao.update(milk)
        milkDataSource.updateMilk(milk)
    }

    override fun getAllMilkFromSms(smsSource: Milk.Source.Sms): List<Milk> {
        return milkDataSourceFromSms.getAllMilk(smsSource)
    }
}