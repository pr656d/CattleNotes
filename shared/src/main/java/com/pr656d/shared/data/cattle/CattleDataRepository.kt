package com.pr656d.shared.data.cattle

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Cattle] data for the presentation layer.
 */
interface CattleRepository {
    fun addCattle(cattle: Cattle)
    fun addAllCattle(cattleList: List<Cattle>)
    fun getObservableAllCattle(): LiveData<List<Cattle>>
    fun getCattleById(id: Long): Cattle?
    fun getCattleByTagNumber(tagNumber: Long): Cattle?
    fun deleteCattle(cattle: Cattle)
    fun updateCattle(cattle: Cattle): Int
}

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) : CattleRepository {

    override fun addCattle(cattle: Cattle) = appDatabase.cattleDao().insert(cattle)

    override fun addAllCattle(cattleList: List<Cattle>) =
        appDatabase.cattleDao().insertAll(cattleList)

    override fun getObservableAllCattle(): LiveData<List<Cattle>> =
        appDatabase.cattleDao().getObservableAll()

    override fun getCattleById(id: Long): Cattle? = appDatabase.cattleDao().getById(id)

    override fun getCattleByTagNumber(tagNumber: Long): Cattle? =
        appDatabase.cattleDao().getByTagNumber(tagNumber)

    override fun deleteCattle(cattle: Cattle) = appDatabase.cattleDao().delete(cattle)

    override fun updateCattle(cattle: Cattle): Int = appDatabase.cattleDao().update(cattle)
}