package com.pr656d.cattlenotes.ui.main.cattle.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    private val _cattle =  MutableLiveData<Cattle>()
    val cattle: LiveData<Event<Cattle>> = Transformations.map(_cattle) { Event(it) }
    fun setCattle(cattle: Cattle) = _cattle.postValue(cattle)

    private val _saving by lazy { MutableLiveData<Boolean>() }
    val saving = _saving

    private val _editMode = MutableLiveData<Boolean>(false)
    val editMode: LiveData<Event<Boolean>> = Transformations.map(_editMode) { Event(it) }

    fun isInEditMode(): Boolean = _editMode.value!!

    fun changeMode() {
        if (_editMode.value!!)
            _editMode.postValue(false)
        else
            _editMode.postValue(true)
    }

    // Start : Holding UI data

    private val _tagNumber by lazy { MutableLiveData<String>() }
    val tagNumber: LiveData<String> = _tagNumber
    fun setTagNumber(s: String) = _tagNumber.postValue(s)
    val showErrorTagNumber: LiveData<Int> = Transformations.map(_tagNumber) {
        validateTagNumber(it)
    }

    private val _name by lazy { MutableLiveData<String>() }
    val name: LiveData<String> = _name
    fun setName(s: String) = _name.postValue(s)

    private val _totalCalving by lazy { MutableLiveData<String>() }
    private val totalCalving: LiveData<String> = _totalCalving
    fun setTotalCalving(s: String) = _totalCalving.postValue(s)
    val showErrorTotalCalving: LiveData<Int> = Transformations.map(_totalCalving) {
        validateTotalCalving(it)
    }

    private val _breed by lazy { MutableLiveData<String>() }
    val breed: LiveData<String> = _breed
    fun setBreed(s: String) = _breed.postValue(s)

    private val _type by lazy { MutableLiveData<String>() }
    val type: LiveData<String> = _type
    fun setType(s: String) = _type.postValue(s)
    val showErrorType: LiveData<Int> = Transformations.map(_type) {
        validateType(it)
    }

    private val _imageUrl by lazy { MutableLiveData<String>() }
    private val imageUrl: LiveData<String> = _imageUrl
    fun setImageUrl(s: String) = _imageUrl.postValue(s)

    private val _group by lazy { MutableLiveData<String>() }
    val group: LiveData<String> = _group
    fun setGroup(s: String) = _group.postValue(s)

    private val _dob by lazy { MutableLiveData<String>() }
    private val dob: LiveData<String> = _dob
    fun setDob(s: String) = _dob.postValue(s)

    private val _aiDate by lazy { MutableLiveData<String>() }
    private val aiDate: LiveData<String> = _aiDate
    fun setAiDate(s: String) = _aiDate.postValue(s)

    private val _repeatHeatDate by lazy { MutableLiveData<String>() }
    private val repeatHeatDate: LiveData<String> = _repeatHeatDate
    fun setRepeatHeatDate(s: String) = _repeatHeatDate.postValue(s)

    private val _pregnancyCheckDate by lazy { MutableLiveData<String>() }
    private val pregnancyCheckDate: LiveData<String> = _pregnancyCheckDate
    fun setPregnancyCheckDate(s: String) = _pregnancyCheckDate.postValue(s)

    private val _dryOffDate by lazy { MutableLiveData<String>() }
    private val dryOffDate: LiveData<String> = _dryOffDate
    fun setDryOffDate(s: String) = _dryOffDate.postValue(s)

    private val _calvingDate by lazy { MutableLiveData<String>() }
    private val calvingDate: LiveData<String> = _calvingDate
    fun setCalvingDate(s: String) = _calvingDate.postValue(s)

    private val _purchaseAmount by lazy { MutableLiveData<Long?>(null) }
    private val purchaseAmount: LiveData<Long?> = _purchaseAmount
    fun setPurchaseAmount(s: Long) = _purchaseAmount.postValue(s)

    private val _purchaseDate by lazy { MutableLiveData<String>() }
    private val purchaseDate: LiveData<String> = _purchaseDate
    fun setPurchaseDate(s: String) = _purchaseDate.postValue(s)

    // End : Holding UI data

    private fun validateTagNumber(tagNumber: String?): Int = runBlocking {
        CattleValidator.isValidTagNumber(tagNumber, cattleDataRepository, _cattle.value!!.tagNumber)
    }

    private fun validateType(type: String?): Int = CattleValidator.isValidType(type)

    private fun validateTotalCalving(totalCalving: String?): Int =
        CattleValidator.isValidTotalCalving(totalCalving)

    private fun validateFields() {
        if (tagNumber.value == null) {
            _tagNumber.postValue(" ")
        }
        if (type.value == null) {
            _type.postValue(" ")
        }
        if (totalCalving.value == null) {
            _totalCalving.postValue(" ")
        }
    }

    fun saveCattle() {
        validateFields()
        val newCattle = getCattle()
        if (
            showErrorTagNumber.value == CattleValidator.VALID_MESSAGE_ID &&
            showErrorType.value == CattleValidator.VALID_MESSAGE_ID &&
            showErrorTotalCalving.value == CattleValidator.VALID_MESSAGE_ID &&
            cattle.value!!.peekContent() != newCattle
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _saving.postValue(true)
                    cattleDataRepository.updateCattle(newCattle)
                    _cattle.postValue(newCattle)
                    _saving.postValue(false)
                } catch (e: Exception) {
                    _saving.postValue(false)
                }
            }
        }
    }

    private fun getCattle(): Cattle =
        Cattle(
            tagNumber.value!!.toLong(), name.value, type.value!!, imageUrl.value, breed.value,
            group.value, totalCalving.value?.toInt() ?: 0, dob.value, aiDate.value,
            repeatHeatDate.value, pregnancyCheckDate.value, dryOffDate.value, calvingDate.value,
            purchaseAmount.value?.toLong(), purchaseDate.value
        )
}