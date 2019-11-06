package com.pr656d.cattlenotes.data.local.db

import androidx.room.*

/**
 * The Data Access Object for the [AnimalEntity] class.
 */
@Dao
interface AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(animal: AnimalEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(animal: AnimalEntity)

    @Delete
    fun delete(animal: AnimalEntity)
}