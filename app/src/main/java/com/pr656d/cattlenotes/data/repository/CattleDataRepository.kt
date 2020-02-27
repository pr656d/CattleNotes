package com.pr656d.cattlenotes.data.repository

import androidx.lifecycle.LiveData
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.model.Cattle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Cattle] data for the presentation layer.
 */
interface CattleRepository {
    fun addCattle(cattle: Cattle)
    fun addAllCattle(cattleList: List<Cattle>)
    fun getObservableAllCattle(): LiveData<List<Cattle>>
    fun getAllCattle(): List<Cattle>
    fun getCattleById(id: Long): Cattle
    fun deleteCattle(cattle: Cattle)
    fun updateCattle(cattle: Cattle): Int
}

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) : CattleRepository {

    override fun addCattle(cattle: Cattle) = appDatabase.cattleDao().insert(cattle)

    override fun addAllCattle(cattleList: List<Cattle>) = appDatabase.cattleDao().insertAll(cattleList)

    override fun getAllCattle(): List<Cattle> = appDatabase.cattleDao().getObservableAll().value ?: emptyList()

    override fun getObservableAllCattle(): LiveData<List<Cattle>> = appDatabase.cattleDao().getObservableAll()

    override fun getCattleById(id: Long): Cattle = appDatabase.cattleDao().getCattle(id)

    override fun deleteCattle(cattle: Cattle) = appDatabase.cattleDao().delete(cattle)

    override fun updateCattle(cattle: Cattle): Int = appDatabase.cattleDao().update(cattle)
}