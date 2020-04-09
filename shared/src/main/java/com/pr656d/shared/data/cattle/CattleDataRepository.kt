package com.pr656d.shared.data.cattle

import androidx.lifecycle.LiveData
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import com.pr656d.shared.data.db.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single point of access for [Cattle] data for the presentation layer.
 */
interface CattleRepository {
    /** @param [saveToLocal] use when this function is called by data source */
    fun addCattle(cattle: Cattle, saveToLocal: Boolean = false)

    fun getAllCattle(): LiveData<List<Cattle>>

    fun getCattleById(id: String): LiveData<Cattle?>

    fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?>

    /** @param [saveToLocal] use when this function is called by data source */
    fun deleteCattle(cattle: Cattle, saveToLocal: Boolean = false)

    /** @param [saveToLocal] use when this function is called by data source */
    fun updateCattle(cattle: Cattle, saveToLocal: Boolean = false)

    /** Returns cattle with matching tag number exist or not */
    fun isCattleExistByTagNumber(tagNumber: Long): Boolean
}

@Singleton
open class CattleDataRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val cattleDataSource: CattleDataSource
) : CattleRepository {

    override fun addCattle(cattle: Cattle, saveToLocal: Boolean) {
        if (saveToLocal)
            appDatabase.cattleDao().insert(cattle)
        else
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

    override fun deleteCattle(cattle: Cattle, saveToLocal: Boolean) {
        if (saveToLocal)
            appDatabase.cattleDao().delete(cattle)
        else
            cattleDataSource.deleteCattle(cattle)
    }

    override fun updateCattle(cattle: Cattle, saveToLocal: Boolean) {
        if (saveToLocal)
            appDatabase.cattleDao().update(cattle)
        else
            cattleDataSource.updateCattle(cattle)
    }

    override fun isCattleExistByTagNumber(tagNumber: Long): Boolean {
        // If count is more than 0 than it exists.
        return appDatabase.cattleDao().getRowCountWithMatchingTagNumber(tagNumber) > 0
    }
}