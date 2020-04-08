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

    fun getObservableAllCattle(): LiveData<List<Cattle>>

    fun getCattleById(id: String): Cattle?

    fun getCattleByTagNumber(tagNumber: String): Cattle?

    /** @param [saveToLocal] use when this function is called by data source */
    fun deleteCattle(cattle: Cattle, saveToLocal: Boolean = false)

    /** @param [saveToLocal] use when this function is called by data source */
    fun updateCattle(cattle: Cattle, saveToLocal: Boolean = false)
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

    override fun getObservableAllCattle(): LiveData<List<Cattle>> =
        appDatabase.cattleDao().getObservableAll()

    override fun getCattleById(id: String): Cattle? = appDatabase.cattleDao().getById(id)

    override fun getCattleByTagNumber(tagNumber: String): Cattle? =
        appDatabase.cattleDao().getByTagNumber(tagNumber)

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
}