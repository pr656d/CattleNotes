package com.pr656d.shared.data.db

import androidx.annotation.WorkerThread

/**
 * [AppDatabase] API surface.
 *
 * Restricts unwanted operations on [AppDatabase] directly.
 */
interface AppDatabaseDao {
    /**
     * Clear all the table entries from the database.
     */
    @WorkerThread
    fun clear()
}