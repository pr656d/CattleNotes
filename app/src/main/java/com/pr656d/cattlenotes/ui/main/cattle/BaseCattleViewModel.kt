package com.pr656d.cattlenotes.ui.main.cattle

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.ui.main.cattle.add.AddCattleViewModel
import com.pr656d.cattlenotes.ui.main.cattle.details.CattleDetailsViewModel
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Common abstract class for [AddCattleViewModel] and [CattleDetailsViewModel]
 * to reduce code repetition.
 */
abstract class BaseCattleViewModel : BaseViewModel() {

    abstract fun provideCattleDataRepository(): CattleDataRepository

    abstract fun provideCurrentTagNumber(): Long?

    private val _saving by lazy { MutableLiveData<Boolean>(false) }
    val saving = _saving

    protected val _showMessage by lazy { MutableLiveData<@StringRes Int>() }
    val showMessage = Transformations.map(_showMessage) { Event(it) }

    private val _showRetrySnackBar by lazy { MutableLiveData<@StringRes Int>() }
    val showRetrySnackBar: LiveData<Int> = _showRetrySnackBar

    // Start : Holding UI data

    private val _tagNumber by lazy { MutableLiveData<String>() }
    val tagNumber: LiveData<String> = _tagNumber
    fun setTagNumber(value: String) = _tagNumber.postValue(value)
    val showErrorOnTagNumber: LiveData<Int> = Transformations.map(_tagNumber) {
        validateTagNumber(it)
    }

    private val _name by lazy { MutableLiveData<String>() }
    val name: LiveData<String> = _name
    fun setName(value: String) = _name.postValue(value)

    private val _totalCalving by lazy { MutableLiveData<String>() }
    private val totalCalving: LiveData<String> = _totalCalving
    fun setTotalCalving(value: String) = _totalCalving.postValue(value)
    val showErrorOnTotalCalving: LiveData<Int> = Transformations.map(_totalCalving) {
        validateTotalCalving(it)
    }

    private val _breed by lazy { MutableLiveData<String>() }
    val breed: LiveData<String> = _breed
    fun setBreed(value: String) = _breed.postValue(value)

    private val _type by lazy { MutableLiveData<String>() }
    val type: LiveData<String> = _type
    fun setType(value: String) = _type.postValue(value)
    val showErrorOnType: LiveData<Int> = Transformations.map(_type) {
        validateType(it)
    }

    private val _imageUrl by lazy { MutableLiveData<String>() }
    private val imageUrl: LiveData<String> = _imageUrl
    fun setImageUrl(value: String) = _imageUrl.postValue(value)

    private val _group by lazy { MutableLiveData<String>() }
    val group: LiveData<String> = _group
    fun setGroup(value: String) = _group.postValue(value)

    private val _dob by lazy { MutableLiveData<String>() }
    private val dob: LiveData<String> = _dob
    fun setDob(value: String) = _dob.postValue(value)

    private val _aiDate by lazy { MutableLiveData<String>() }
    private val aiDate: LiveData<String> = _aiDate
    fun setAiDate(value: String) = _aiDate.postValue(value)

    private val _repeatHeatDate by lazy { MutableLiveData<String>() }
    private val repeatHeatDate: LiveData<String> = _repeatHeatDate
    fun setRepeatHeatDate(value: String) = _repeatHeatDate.postValue(value)

    private val _pregnancyCheckDate by lazy { MutableLiveData<String>() }
    private val pregnancyCheckDate: LiveData<String> = _pregnancyCheckDate
    fun setPregnancyCheckDate(value: String) = _pregnancyCheckDate.postValue(value)

    private val _dryOffDate by lazy { MutableLiveData<String>() }
    private val dryOffDate: LiveData<String> = _dryOffDate
    fun setDryOffDate(value: String) = _dryOffDate.postValue(value)

    private val _calvingDate by lazy { MutableLiveData<String>() }
    private val calvingDate: LiveData<String> = _calvingDate
    fun setCalvingDate(value: String) = _calvingDate.postValue(value)

    private val _purchaseAmount by lazy { MutableLiveData<Long?>(null) }
    private val purchaseAmount: LiveData<Long?> = _purchaseAmount
    fun setPurchaseAmount(value: Long?) = _purchaseAmount.postValue(value)

    private val _purchaseDate by lazy { MutableLiveData<String>() }
    private val purchaseDate: LiveData<String> = _purchaseDate
    fun setPurchaseDate(value: String) = _purchaseDate.postValue(value)

    // End : Holding UI data

    private fun validateTagNumber(tagNumber: String?): Int = runBlocking {
        CattleValidator.isValidTagNumber(tagNumber, provideCattleDataRepository(), provideCurrentTagNumber())
    }

    private fun validateType(type: String?): Int = CattleValidator.isValidType(type)

    private fun validateTotalCalving(totalCalving: String?): Int =
        CattleValidator.isValidTotalCalving(totalCalving)

    /**
     * Function will be called by inherited class.
     * By taking suspending lambda function as parameter for success and failure
     * to do more operations after on success and on failure.
     *
     * Allows calls like:
     * 1.   saveCattle(
     *          doOnSuccess = {
     *              // Do something on success.
     *          },
     *          doOnFailure = {
     *              // Do something on failure.
     *          }
     *      )
     *
     *  2. saveCattle()
     */
    fun saveCattle(
        doOnSuccess: suspend () -> Unit = {},
        doOnFailure: suspend () -> Unit = {}
    ) {
        val toggleSaving: suspend () -> Unit = {
            withContext(Dispatchers.Main) {
                _saving.value = _saving.value!!.not()
            }
        }

        if (
            showErrorOnTagNumber.value == CattleValidator.VALID_MESSAGE_ID &&
            showErrorOnType.value == CattleValidator.VALID_MESSAGE_ID &&
            showErrorOnTotalCalving.value == CattleValidator.VALID_MESSAGE_ID
        )
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    toggleSaving()
                    val newCattle = getCattle()
                    provideCattleDataRepository().addCattle(newCattle)
                    toggleSaving()
                    doOnSuccess()
                } catch (e: Exception) {
                    doOnFailure()
                    toggleSaving()
                    _showRetrySnackBar.postValue(R.string.retry)
                }
            }
        else
            _showMessage.postValue(R.string.error_fill_empty_fields)
    }

    protected fun getCattle(): Cattle =
        Cattle(
            tagNumber.value!!.toLong(), name.value, type.value!!, imageUrl.value, breed.value,
            group.value, totalCalving.value?.toInt() ?: 0, dob.value, aiDate.value,
            repeatHeatDate.value, pregnancyCheckDate.value, dryOffDate.value, calvingDate.value,
            purchaseAmount.value?.toLong(), purchaseDate.value
        )
}