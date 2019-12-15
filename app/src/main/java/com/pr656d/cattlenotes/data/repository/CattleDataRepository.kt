package com.pr656d.cattlenotes.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.utils.common.toCattle
import com.pr656d.cattlenotes.shared.utils.common.toCattleEntity
import com.pr656d.cattlenotes.shared.utils.common.toCattleEntityList
import com.pr656d.cattlenotes.shared.utils.common.toCattleList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    suspend fun addCattle(cattle: Cattle) =
        appDatabase.cattleDao().insert(cattle.toCattleEntity())

    suspend fun addAllCattle(cattleList: List<Cattle>) =
        appDatabase.cattleDao().insertAll(cattleList.toCattleEntityList())

    suspend fun isCattleExists(tagNumber: Long): Boolean {
        return try {
            val c = getCattle(tagNumber)
            c != null
        } catch (e: Exception) {
            false
        }
    }

    fun getAllCattle(): LiveData<List<Cattle>> =
        appDatabase.cattleDao().getAll().run {
            Transformations.map(this) { it.toCattleList() }
        }

    suspend fun getCattle(tagNumber: Long): Cattle? =
        appDatabase.cattleDao().getCattle(tagNumber)?.toCattle()

    suspend fun deleteCattle(cattle: Cattle) =
        appDatabase.cattleDao().delete(cattle.toCattleEntity())

    suspend fun updateCattle(cattle: Cattle) =
        appDatabase.cattleDao().update(cattle.toCattleEntity())
}