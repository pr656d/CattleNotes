package com.pr656d.cattlenotes.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pr656d.cattlenotes.data.model.Cattle

/**
 * The Data Access Object for the [Cattle] class.
 */
@Dao
interface CattleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cattle: Cattle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cattle: List<Cattle>)

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattle_list")
    fun getAll(): LiveData<List<Cattle>>

    @Query("SELECT * FROM cattle_list WHERE tag_number == :tagNumber")
    suspend fun getCattle(tagNumber: String): Cattle?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(cattle: Cattle)

    @Delete
    suspend fun delete(vararg cattle: Cattle)
}