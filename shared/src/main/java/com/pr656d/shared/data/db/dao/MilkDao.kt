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

package com.pr656d.shared.data.db.dao

import androidx.room.*
import com.pr656d.model.Milk
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Milk] class.
 */
@Dao
interface MilkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(milk: Milk) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(milkList: List<Milk>): List<Long>

    @Query("SELECT * FROM milkList ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Milk>>

    @Query("SELECT * FROM milkList WHERE id == :id")
    fun getById(id: String): Flow<Milk?>

    @Update
    suspend fun update(milk: Milk)

    @Delete
    suspend fun delete(milk: Milk)
}