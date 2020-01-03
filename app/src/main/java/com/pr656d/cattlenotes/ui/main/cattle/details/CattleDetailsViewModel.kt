package com.pr656d.cattlenotes.ui.main.cattle.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.toFormattedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseCattleViewModel() {

    private val _editMode = MutableLiveData<Boolean>(false)
    val editMode: LiveData<Boolean> = _editMode

    fun isInEditMode(): Boolean = _editMode.value!!

    private fun changeMode() {
        _editMode.value = _editMode.value!!.not()
    }

    override fun provideCattleDataRepository(): CattleDataRepository = cattleDataRepository

    override fun provideCurrentTagNumber(): String? = tagNumber.value

    fun onEditSaveClick() {
        if (isInEditMode())
            saveCattle(
                doOnSuccess = {
                    withContext(Dispatchers.Main) {
                        changeMode()
                        _showMessage.postValue(R.string.cattle_saved)
                    }
                }
            )
        else
            changeMode()
    }

    fun fetchCattle(tagNumber: String) {
        viewModelScope.launch {
            cattleDataRepository.getCattle(tagNumber)?.bindData()
        }
    }

    private fun Cattle.bindData() {
        setTagNumber(tagNumber)
        setName(name)
        setType(type.displayName)
        setBreed(breed.displayName)
        setGroup(group.displayName)
        setLactation(lactation.toString())
        setDob(dateOfBirth?.toFormattedString())
        setParent(parent)
        setHomeBorn(homeBirth)
        setPurchaseAmount(purchaseAmount?.toString())
        setPurchaseDate(purchasedOn?.toFormattedString())
    }
}