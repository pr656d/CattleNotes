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

import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject

/**
 * [AppDatabase] API surface.
 *
 * Restricts unwanted operations on [AppDatabase] directly.
 */
interface AppDatabaseDao {
    /**
     * Clear all the table entries from the database.
     */
    suspend fun clear()
}

class AppDatabaseDaoImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : AppDatabaseDao {
    override suspend fun clear() {
        appDatabase.clearAllTables()
    }
}