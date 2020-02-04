package com.pr656d.cattlenotes.data.repository

import androidx.lifecycle.LiveData
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.model.Cattle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    suspend fun addCattle(cattle: Cattle) = appDatabase.cattleDao().insert(cattle)

    suspend fun addAllCattle(cattleList: List<Cattle>) = appDatabase.cattleDao().insertAll(cattleList)

    fun getAllCattle(): LiveData<List<Cattle>> = appDatabase.cattleDao().getAll()

    suspend fun getCattle(id: Long): Cattle? = appDatabase.cattleDao().getCattle(id)

    suspend fun deleteCattle(cattle: Cattle) = appDatabase.cattleDao().delete(cattle)

    suspend fun updateCattle(cattle: Cattle): Int = appDatabase.cattleDao().update(cattle)
}