package com.pr656d.cattlenotes.ui.main.cattle.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseCattleViewModel() {

    private val _cattle =  MutableLiveData<Cattle>()
    val cattle: LiveData<Event<Cattle>> = Transformations.map(_cattle) { Event(it) }
    fun setCattle(cattle: Cattle) = _cattle.postValue(cattle)

    private val _editMode = MutableLiveData<Boolean>(false)
    val editMode: LiveData<Boolean> = _editMode

    fun isInEditMode(): Boolean = _editMode.value!!

    private fun changeMode() = _editMode.postValue(_editMode.value!!.not())

    override fun provideCattleDataRepository(): CattleDataRepository = cattleDataRepository

    override fun provideCurrentTagNumber(): Long? = _cattle.value!!.tagNumber

    fun onFabClick() =
        if (isInEditMode())
            saveCattle(
                doOnSuccess = {
                    changeMode()
                },
                doOnFailure = {
                    changeMode()
                }
            )
        else
            changeMode()
}