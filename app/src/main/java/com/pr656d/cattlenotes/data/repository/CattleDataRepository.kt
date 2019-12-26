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

    suspend fun isCattleExists(tagNumber: Long): Boolean =
        try {
            getCattle(tagNumber) != null
        } catch (e: Exception) {
            false
        }

    fun getAllCattle(): LiveData<List<Cattle>> = appDatabase.cattleDao().getAll()

    suspend fun getCattle(tagNumber: Long): Cattle? = appDatabase.cattleDao().getCattle(tagNumber)

    suspend fun deleteCattle(cattle: Cattle) = appDatabase.cattleDao().delete(cattle)

    suspend fun updateCattle(cattle: Cattle) = appDatabase.cattleDao().update(cattle)
}