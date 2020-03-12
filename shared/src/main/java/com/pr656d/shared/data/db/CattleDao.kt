package com.pr656d.shared.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pr656d.model.Cattle

/**
 * The Data Access Object for the [Cattle] class.
 */
@Dao
interface CattleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cattle: Cattle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cattle: List<Cattle>)

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattle_list ORDER BY tag_number")
    fun getObservableAll(): LiveData<List<Cattle>>

    @Query("SELECT * FROM cattle_list WHERE id == :id")
    fun getCattleById(id: Long): Cattle?

    @Query("SELECT * FROM cattle_list WHERE tag_number == :tagNumber")
    fun getCattleByTagNumber(tagNumber: Long): Cattle?

    @Update
    fun update(cattle: Cattle): Int

    @Delete
    fun delete(vararg cattle: Cattle)
}