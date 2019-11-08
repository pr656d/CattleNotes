package com.pr656d.cattlenotes.data.repository

import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.data.local.db.CattleEntity
import com.pr656d.cattlenotes.model.Cattle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    fun addAnimal(animal: Cattle) {
        appDatabase.cattleDao().insert(
            CattleEntity(
                animal.tagNumber,
                animal.name,
                animal.type.displayName,
                animal.breed?.displayName,
                animal.group?.displayName,
                animal.calving,
                animal.aiDate,
                animal.repeatHeatDate,
                animal.pregnancyCheckDate,
                animal.dryOffDate
            )
        )
    }
}