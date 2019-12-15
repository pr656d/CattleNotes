package com.pr656d.cattlenotes.ui.main.cattle.details

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseCattleViewModel() {

    private val _cattle =  MutableLiveData<Cattle>()
    val cattle: LiveData<Event<Cattle>> = Transformations.map(_cattle) { Event(it) }
    fun setCattle(cattle: Cattle) = _cattle.postValue(cattle)

    private val _showError by lazy { MutableLiveData<@StringRes Int>() }
    val showError = Transformations.map(_showError) { Event(it) }

    private val _editMode = MutableLiveData<Boolean>(false)
    val editMode: LiveData<Boolean> = _editMode

    fun isInEditMode(): Boolean = _editMode.value!!

    private fun changeMode() = _editMode.postValue(_editMode.value!!.not())

    override fun provideCattleDataRepository(): CattleDataRepository = cattleDataRepository

    override fun provideCurrentTagNumber(): Long? = _cattle.value!!.tagNumber

    fun retrySave() = onEditSaveClick()

    fun onEditSaveClick() =
        if (isInEditMode())
            saveCattle(
                doOnSuccess = {
                    withContext(Dispatchers.Main) {
                        changeMode()
                    }
                },
                doOnFailure = {
                    withContext(Dispatchers.Main) {
                        _showError.postValue(R.string.error_save)
                        changeMode()
                    }
                }
            )
        else
            changeMode()
}