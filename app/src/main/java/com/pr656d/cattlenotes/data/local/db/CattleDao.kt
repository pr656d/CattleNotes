package com.pr656d.cattlenotes.data.local.db

import androidx.lifecycle.LiveData
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

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattle_list")
    fun getAll(): LiveData<List<CattleEntity>>

    @Query("SELECT * FROM cattle_list WHERE tagNumber == :tagNumber")
    suspend fun getCattle(tagNumber: Long): CattleEntity?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(cattle: CattleEntity)

    @Delete
    suspend fun delete(cattle: CattleEntity)
}