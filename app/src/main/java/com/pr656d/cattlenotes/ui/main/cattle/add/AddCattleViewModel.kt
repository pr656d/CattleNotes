package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class AddCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseCattleViewModel() {

    private val _navigateUp by lazy { MutableLiveData<Unit>() }
    val navigateUp = Transformations.map(_navigateUp) { Event(it) }

    override fun provideCattleDataRepository(): CattleDataRepository =  cattleDataRepository

    override fun provideCurrentTagNumber(): String? = null

    fun onSaveClick() = saveCattle(
        doOnSuccess = {
            _navigateUp.postValue(Unit)
        }
    )
}