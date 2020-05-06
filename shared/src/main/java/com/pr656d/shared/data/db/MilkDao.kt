package com.pr656d.shared.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pr656d.model.Milk

/**
 * The Data Access Object for the [Milk] class.
 */
@Dao
interface MilkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(milk: Milk)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(milkList: List<Milk>): List<Long>

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM milkList ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<Milk>>

    @Query("SELECT * FROM milkList ORDER BY timestamp DESC")
    fun getAllUnobserved(): List<Milk>

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM milkList WHERE id == :id")
    fun getById(id: String): LiveData<Milk?>

    /**
     * Returns number of rows matches with tag number.
     * Useful when we want to check row exists or not. If count is more than 0 it exists.
     */
    @Query("SELECT COUNT(id) FROM milkList WHERE id == :id")
    fun getRowCountWithMatchingTagNumber(id: Long): Int

    @Update
    fun update(milk: Milk)

    @Delete
    fun delete(vararg milk: Milk)
}