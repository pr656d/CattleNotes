package com.pr656d.cattlenotes.data.local.db

import androidx.room.*

/**
 * The Data Access Object for the [CattleEntity] class.
 */
@Dao
interface CattleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cattle: CattleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cattle: List<CattleEntity>)

    @Query("SELECT * FROM cattle_list")
    suspend fun getAll(): List<CattleEntity>

    @Query("SELECT * FROM cattle_list WHERE tagNumber == :tagNumber")
    suspend fun getCattle(tagNumber: String): CattleEntity

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(cattle: CattleEntity)

    @Delete
    suspend fun delete(cattle: CattleEntity)
}