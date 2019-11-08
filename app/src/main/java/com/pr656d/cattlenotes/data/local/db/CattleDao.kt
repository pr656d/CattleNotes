package com.pr656d.cattlenotes.data.local.db

import androidx.room.*

/**
 * The Data Access Object for the [CattleEntity] class.
 */
@Dao
interface CattleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(animal: CattleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(animal: List<CattleEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(animal: CattleEntity)

    @Delete
    fun delete(animal: CattleEntity)
}