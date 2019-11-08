package com.pr656d.cattlenotes.data.repository

import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.utils.common.LoadSampleData
import com.pr656d.cattlenotes.shared.utils.common.toCattleEntity
import com.pr656d.cattlenotes.shared.utils.common.toCattleEntityList
import com.pr656d.cattlenotes.shared.utils.common.toCattleList
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    fun loadSampleData(): Completable =
        appDatabase.cattleDao().insertAll(LoadSampleData.getListOfCattleEntity())

    fun addCattle(cattle: Cattle): Completable =
        appDatabase.cattleDao().insert(cattle.toCattleEntity())

    fun addAllCattle(cattleList: List<Cattle>): Completable =
        appDatabase.cattleDao().insertAll(cattleList.toCattleEntityList())

    fun getAllCattle(): Single<List<Cattle>> =
        appDatabase.cattleDao().getAll().map { it.toCattleList() }

    fun deleteCattle(cattle: Cattle): Completable =
        appDatabase.cattleDao().delete(cattle.toCattleEntity())

    fun updateCattle(cattle: Cattle): Completable =
        appDatabase.cattleDao().update(cattle.toCattleEntity())
}