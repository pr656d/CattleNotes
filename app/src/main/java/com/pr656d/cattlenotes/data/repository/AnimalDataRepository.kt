package com.pr656d.cattlenotes.data.repository

import com.pr656d.cattlenotes.data.local.db.AnimalEntity
import com.pr656d.cattlenotes.data.local.db.AppDatabase
import com.pr656d.cattlenotes.model.Animal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AnimalDataRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {

    fun addAnimal(animal: Animal) {
        appDatabase.animalDao().insert(
            AnimalEntity(
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