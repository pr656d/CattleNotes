package com.pr656d.shared.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pr656d.model.BreedingCycle

/**
 * The Data Access Object for the [BreedingCycle] class.
 */
@Dao
interface BreedingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(breedingCycle: BreedingCycle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(breedingCycle: List<BreedingCycle>)

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM breeding_cycle ORDER BY cattle_id")
    fun getObservableAll(): LiveData<List<BreedingCycle>>

    @Query("SELECT * FROM breeding_cycle WHERE cattle_id == :cattleId")
    fun getByCattleId(cattleId: Long): BreedingCycle?

    @Update
    fun update(breedingCycle: BreedingCycle): Int

    @Delete
    fun delete(vararg breedingCycle: BreedingCycle)
}