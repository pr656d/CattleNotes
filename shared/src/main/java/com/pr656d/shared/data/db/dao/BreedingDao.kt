/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Breeding] class.
 */
@Dao
interface BreedingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breeding: Breeding): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(breedingList: List<Breeding>): List<Long>

    @Query("SELECT * FROM breedingList ORDER BY cattleId")
    fun getAll(): Flow<List<Breeding>>

    @Query("SELECT * FROM breedingList WHERE id == :breedingId")
    fun getById(breedingId: String): Flow<Breeding?>

    @Transaction
    @Query("SELECT * FROM breedingList")
    fun getAllBreedingWithCattle(): Flow<List<BreedingWithCattle>>

    @Query("SELECT * FROM breedingList WHERE cattleId == :cattleId")
    fun getAllByCattleId(cattleId: String): Flow<List<Breeding>>

    @Transaction
    @Query("SELECT * FROM breedingList WHERE id == :breedingId")
    fun getBreedingWithCattle(breedingId: String): Flow<BreedingWithCattle?>

    @Transaction
    @Query("SELECT * FROM breedingList WHERE cattleId == :cattleId")
    fun getAllBreedingWithCattleByCattleId(cattleId: String): Flow<List<BreedingWithCattle>>

    @Update
    suspend fun update(breeding: Breeding)

    @Delete
    suspend fun delete(breeding: Breeding)
}
