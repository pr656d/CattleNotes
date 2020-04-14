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
    fun insertAll(cattle: List<Cattle>): List<Long>

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattleList ORDER BY tagNumber")
    fun getAll(): LiveData<List<Cattle>>

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattleList WHERE id == :id")
    fun getById(id: String): LiveData<Cattle?>

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattleList WHERE tagNumber == :tagNumber")
    fun getByTagNumber(tagNumber: Long): LiveData<Cattle?>

    /**
     * Returns number of rows matches with tag number.
     * Useful when we want to check row exists or not. If count is more than 0 it exists.
     */
    @Query("SELECT COUNT(id) FROM cattleList WHERE tagNumber == :tagNumber")
    fun getRowCountWithMatchingTagNumber(tagNumber: Long): Int

    @Update
    fun update(cattle: Cattle)

    @Delete
    fun delete(vararg cattle: Cattle)
}