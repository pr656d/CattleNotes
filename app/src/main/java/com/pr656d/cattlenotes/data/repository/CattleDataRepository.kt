package com.pr656d.cattlenotes.data.repository

import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.local.db.CattleEntity
import com.pr656d.cattlenotes.model.Cattle
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
        appDatabase.cattleDao().insertAll(
            arrayListOf<CattleEntity>().apply {
                add(CattleEntity(
                    "764538726128", "Janki", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.CALF_FEMALE.displayName
                ))
                add(CattleEntity(
                    "972349683764", "Sita", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.MILKING.displayName
                ))
                add(CattleEntity(
                    "098386547654", "Parvati", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.MILKING.displayName
                ))
                add(CattleEntity(
                    "981265127629", type = Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.DRY.displayName
                ))
                add(CattleEntity(
                    "987908907398", "Janu", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.DRY.displayName
                ))
                add(CattleEntity(
                    "256347889780", "Lakshmi", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.DRY.displayName
                ))
                add(CattleEntity(
                    "786576523465", type = Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.DRY.displayName
                ))
                add(CattleEntity(
                    "785323476563", "Janki", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.MILKING.displayName
                ))
                add(CattleEntity(
                    "908767862267", "Janki", Cattle.CattleType.COW.displayName,
                    group = Cattle.CattleGroup.DRY.displayName
                ))
            }
        )

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