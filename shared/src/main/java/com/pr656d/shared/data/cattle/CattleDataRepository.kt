package com.pr656d.shared.data.cattle

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Cattle] data for the presentation layer.
 *
 * Info: Data will be loaded by data source. CRUD operations will be done on
 * Local DB as well as at data source to optimise operation count.
 */
interface CattleRepository {
    fun addCattle(cattle: Cattle)

    fun getAllCattle(): LiveData<List<Cattle>>

    fun getCattleById(id: String): LiveData<Cattle?>

    fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?>

    fun deleteCattle(cattle: Cattle)

    fun updateCattle(cattle: Cattle)

    /** Returns cattle with matching tag number exist or not */
    fun isCattleExistByTagNumber(tagNumber: Long): Boolean
}

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val cattleDataSource: CattleDataSource
) : CattleRepository {

    /**
     * Add cattle at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun addCattle(cattle: Cattle) {
        appDatabase.cattleDao().insert(cattle)
        cattleDataSource.addCattle(cattle)
    }

    override fun getAllCattle(): LiveData<List<Cattle>> {
        return appDatabase.cattleDao().getAll()
    }

    override fun getCattleById(id: String): LiveData<Cattle?> {
        return appDatabase.cattleDao().getById(id)
    }

    override fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?> {
        return appDatabase.cattleDao().getByTagNumber(tagNumber)
    }

    /**
     * Delete cattle at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun deleteCattle(cattle: Cattle) {
        appDatabase.cattleDao().delete(cattle)
        cattleDataSource.deleteCattle(cattle)
    }

    /**
     * Update cattle at Local DB and at data source also. To optimise CRUD operations count.
     */
    override fun updateCattle(cattle: Cattle) {
        appDatabase.cattleDao().update(cattle)
        cattleDataSource.updateCattle(cattle)
    }

    override fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
        // If count is more than 0 than it exists.
        return appDatabase.cattleDao().getRowCountWithMatchingTagNumber(tagNumber) > 0
    }
}