package com.pr656d.cattlenotes.ui.main.cattle

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.log.Logger
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.ui.main.cattle.add.AddCattleViewModel
import com.pr656d.cattlenotes.ui.main.cattle.details.CattleDetailsViewModel
import com.pr656d.cattlenotes.utils.common.*
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

    abstract fun provideCurrentTagNumber(): String?

    private val _saving by lazy { MutableLiveData<Boolean>(false) }
    val saving = _saving

    protected val _showMessage by lazy { MutableLiveData<@StringRes Int>() }
    val showMessage = Transformations.map(_showMessage) { Event(it) }

    // Start : Holding UI data

    private val _imageUrl by lazy { MutableLiveData<String>() }
    val imageUrl: LiveData<String> = _imageUrl
    fun setImageUrl(value: String) = _imageUrl.postValue(value)

    private val _tagNumber by lazy { MutableLiveData<String>() }
    val tagNumber: LiveData<String> = _tagNumber
    fun setTagNumber(value: String) = _tagNumber.postValue(value)
    val showErrorOnTagNumber: LiveData<Int> = Transformations.map(_tagNumber) {
        validateTagNumber(it)
    }

    private val _name by lazy { MutableLiveData<String?>(null) }
    val name: LiveData<String?> = _name
    fun setName(value: String?) = _name.postValue(value)

    private val _type by lazy { MutableLiveData<String>() }
    val type: LiveData<String> = _type
    fun setType(value: String) = _type.postValue(value)
    val showErrorOnType: LiveData<Int> = Transformations.map(_type) {
        validateType(it)
    }

    private val _breed by lazy { MutableLiveData<String>() }
    val breed: LiveData<String> = _breed
    fun setBreed(value: String) = _breed.postValue(value)
    val showErrorOnBreed: LiveData<Int> = Transformations.map(_breed) {
        validateBreed(it)
    }

    private val _group by lazy { MutableLiveData<String>() }
    val group: LiveData<String> = _group
    fun setGroup(value: String) = _group.postValue(value)
    val showErrorOnGroup: LiveData<Int> = Transformations.map(_group) {
        validateGroup(it)
    }

    private val _lactation by lazy { MutableLiveData<String>("0") }
    val lactation: LiveData<String> = _lactation
    fun setLactation(value: String) = _lactation.postValue(value)
    val showErrorOnLactation: LiveData<Int> = Transformations.map(_lactation) {
        validateLactation(it)
    }

    private val _dob by lazy { MutableLiveData<String?>(null) }
    val dob: LiveData<String?> = _dob
    fun setDob(value: String?) = _dob.postValue(value.getNullOrValue())

    private val _parent by lazy { MutableLiveData<String?>(null) }
    val parent: LiveData<String?> = _parent
    fun setParent(value: String?) = _parent.postValue(value)

    private val _homeBorn by lazy { MutableLiveData<Boolean>(false) }
    val homeBorn: LiveData<Boolean> = _homeBorn
    fun setHomeBorn(value: Boolean) = _homeBorn.postValue(value)

    private val _purchaseAmount by lazy { MutableLiveData<String?>(null) }
    val purchaseAmount: LiveData<String?> = _purchaseAmount
    fun setPurchaseAmount(value: String?) = _purchaseAmount.postValue(value.getNullOrValue())
    val showErrorOnPurchaseAmount: LiveData<Int> = Transformations.map(_purchaseAmount) {
        validatePurchaseAmount(it)
    }

    private val _purchaseDate by lazy { MutableLiveData<String?>(null) }
    val purchaseDate: LiveData<String?> = _purchaseDate
    fun setPurchaseDate(value: String?) = _purchaseDate.postValue(value.getNullOrValue())

    // End : Holding UI data

    private fun validateTagNumber(tagNumber: String?): Int = runBlocking {
        CattleValidator.isValidTagNumber(tagNumber, provideCattleDataRepository(), provideCurrentTagNumber())
    }

    private fun validateType(type: String?): Int = CattleValidator.isValidType(type)

    private fun validateLactation(totalCalving: String?): Int = CattleValidator.isValidLactation(totalCalving)

    private fun validateBreed(breed: String?): Int = CattleValidator.isValidBreed(breed)

    private fun validateGroup(group: String?): Int = CattleValidator.isValidGroup(group)

    private fun validatePurchaseAmount(amount: String?): Int = CattleValidator.isValidAmount(amount)

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
            showErrorOnLactation.value == CattleValidator.VALID_MESSAGE_ID &&
            showErrorOnBreed.value == CattleValidator.VALID_MESSAGE_ID &&
            showErrorOnPurchaseAmount.value == CattleValidator.VALID_MESSAGE_ID
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    toggleSaving()
                    provideCattleDataRepository().run {
                        val cattle = getCattle()
                        if (isCattleExists(cattle.tagNumber))
                            updateCattle(cattle)
                        else
                            addCattle(cattle)
                    }
                    toggleSaving()
                    doOnSuccess()
                } catch (e: Exception) {
                    toggleSaving()
                    doOnFailure()
                    _showMessage.postValue(R.string.retry)
                    Logger.d("save", "${e.printStackTrace()}")
                }
            }
        } else {
            _showMessage.postValue(R.string.error_fill_empty_fields)
        }
    }

    protected fun getCattle(): Cattle =
        Cattle(
            tagNumber.value!!, name.value?.let { it }, null, type.value!!.toType(),
            breed.value!!.toBreed(), group.value!!.toGroup(), lactation.value?.toInt() ?: 0,
            homeBorn.value!!, purchaseAmount.value?.toLong(), purchaseDate.value?.toDate(),
            dob.value?.toDate(), parent.value?.let { it }
        )
}