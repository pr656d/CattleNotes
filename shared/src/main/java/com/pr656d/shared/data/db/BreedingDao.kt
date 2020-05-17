/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    fun insertAll(breedingList: List<Breeding>): List<Long>

    /**
     * Internally Room will handle it on background thread.
     * If any change happens observer will be called by Room.
     */
    @Query("SELECT * FROM breedingList ORDER BY cattleId")
    fun getAll(): LiveData<List<Breeding>>

    @Query("SELECT * FROM breedingList WHERE id == :breedingId")
    fun get(breedingId: String): LiveData<Breeding?>

    @Transaction
    @Query("SELECT * FROM breedingList")
    fun getAllBreedingWithCattle(): LiveData<List<BreedingWithCattle>>

    @Query("SELECT * FROM breedingList WHERE cattleId == :cattleId")
    fun getAllByCattleId(cattleId: String): LiveData<List<Breeding>>

    @Transaction
    @Query("SELECT * FROM breedingList WHERE id == :breedingId")
    fun getBreedingWithCattle(breedingId: String): LiveData<BreedingWithCattle?>

    @Transaction
    @Query("SELECT * FROM breedingList WHERE cattleId == :cattleId")
    fun getAllBreedingWithCattleByCattleId(cattleId: String): LiveData<List<BreedingWithCattle>>

    @Update
    fun update(breeding: Breeding)

    @Delete
    fun delete(vararg breeding: Breeding)
}