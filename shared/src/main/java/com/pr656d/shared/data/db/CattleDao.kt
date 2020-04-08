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

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM cattleList ORDER BY tagNumber")
    fun getObservableAll(): LiveData<List<Cattle>>

    @Query("SELECT * FROM cattleList WHERE id == :id")
    fun getById(id: String): Cattle?

    @Query("SELECT * FROM cattleList WHERE tagNumber == :tagNumber")
    fun getByTagNumber(tagNumber: String): Cattle?

    @Update
    fun update(cattle: Cattle)

    @Delete
    fun delete(vararg cattle: Cattle)
}