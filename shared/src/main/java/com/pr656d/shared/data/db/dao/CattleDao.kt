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
import com.pr656d.model.Cattle
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Cattle] class.
 */
@Dao
interface CattleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cattle: Cattle): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cattle: List<Cattle>): List<Long>

    @Query("SELECT * FROM cattleList ORDER BY tagNumber")
    fun getAll(): Flow<List<Cattle>>

    @Query("SELECT * FROM cattleList WHERE id == :id")
    fun getById(id: String): Flow<Cattle?>

    @Query("SELECT * FROM cattleList WHERE tagNumber == :tagNumber")
    fun getByTagNumber(tagNumber: Long): Flow<Cattle?>

    /**  Returns number of rows matches with tag number.  */
    @Query("SELECT COUNT(id) FROM cattleList WHERE tagNumber == :tagNumber")
    suspend fun getRowCountWithMatchingTagNumber(tagNumber: Long): Int

    @Update
    suspend fun update(cattle: Cattle)

    @Delete
    suspend fun delete(cattle: Cattle)
}