package com.pr656d.shared.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle

/**
 * The Data Access Object for the [Breeding] class.
 */
@Dao
interface BreedingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(breeding: Breeding)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(breeding: List<Breeding>)

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM breedingList ORDER BY cattleId")
    fun getObservableAll(): LiveData<List<Breeding>>

    @Transaction
    @Query("SELECT * FROM breedingList")
    fun getAllBreedingCycleWithCattle(): List<BreedingWithCattle>

    @Query("SELECT * FROM breedingList WHERE cattleId == :cattleId")
    fun getAllByCattleId(cattleId: String): List<Breeding>

    @Update
    fun update(breeding: Breeding): Int

    @Delete
    fun delete(vararg breeding: Breeding)
}