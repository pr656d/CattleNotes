package com.pr656d.cattlenotes.data.local.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * The Data Access Object for the [CattleEntity] class.
 */
@Dao
interface CattleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cattle: CattleEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cattle: List<CattleEntity>): Completable

    @Query("SELECT * FROM cattle")
    fun getAll(): Single<List<CattleEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(cattle: CattleEntity): Completable

    @Delete
    fun delete(cattle: CattleEntity): Completable
}