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