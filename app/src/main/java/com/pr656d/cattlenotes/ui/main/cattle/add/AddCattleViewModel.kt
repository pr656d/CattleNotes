package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class AddCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseCattleViewModel() {

    private val _navigateUp by lazy { MutableLiveData<Cattle>() }
    val navigateUp = Transformations.map(_navigateUp) { Event(it) }

    private val _showError by lazy { MutableLiveData<@StringRes Int>() }
    val showError = Transformations.map(_showError) { Event(it) }

    override fun provideCattleDataRepository(): CattleDataRepository =  cattleDataRepository

    override fun provideCurrentTagNumber(): Long? = null

    fun retrySave() = onSaveClick()

    fun onSaveClick() =
        saveCattle(
            doOnSuccess = {
                _navigateUp.postValue(getCattle())
            },
            doOnFailure = {
                _showError.postValue(R.string.error_save)
            }
        )
}