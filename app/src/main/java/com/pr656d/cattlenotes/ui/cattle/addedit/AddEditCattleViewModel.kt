package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.AddCattleUseCase
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.UpdateCattleUseCase
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.validator.CattleValidatorUseCase
import com.pr656d.cattlenotes.shared.domain.result.Event
import com.pr656d.cattlenotes.shared.domain.result.Result
import com.pr656d.cattlenotes.shared.domain.result.Result.Error
import com.pr656d.cattlenotes.shared.domain.result.Result.Success
import com.pr656d.cattlenotes.utils.toBreed
import com.pr656d.cattlenotes.utils.toGroup
import com.pr656d.cattlenotes.utils.toType
import java.util.*
import javax.inject.Inject

class AddEditCattleViewModel @Inject constructor(
    private val addCattleUseCase: AddCattleUseCase,
    private val updateCattleUseCase: UpdateCattleUseCase,
    cattleValidatorUseCase: CattleValidatorUseCase
) : ViewModel() {

    companion object {
        const val TAG = "AddEditCattleViewModel"
    }

    private var oldCattle: Cattle? = null

    val tagNumber = MutableLiveData<String>()
    private val _tagNumberErrorMessage = MediatorLiveData<@StringRes Int>()
    val tagNumberErrorMessage: LiveData<Int>
        get() = _tagNumberErrorMessage

    val name = MutableLiveData<String?>()

    private val _type = MutableLiveData<String>()
    fun setType(text: String?) { _type.value = text }
    val type: LiveData<String>
        get() = _type
    val typeErrorMessage: LiveData<Int> = type.map {
        cattleValidatorUseCase.isValidType(it)
    }

    private val _breed = MutableLiveData<String>()
    fun setBreed(text: String?) { _breed.value = text }
    val breed: LiveData<String>
        get() = _breed
    val breedErrorMessage: LiveData<Int> = _breed.map {
        cattleValidatorUseCase.isValidBreed(it)
    }

    private val _group = MutableLiveData<String>()
    fun setGroup(text: String?) { _group.value = text }
    val group: LiveData<String>
        get() = _group
    val groupErrorMessage: LiveData<Int> = _group.map {
        cattleValidatorUseCase.isValidGroup(it)
    }

    val lactation = MutableLiveData<String>()
    val lactationErrorMessage: LiveData<Int> = lactation.map {
        cattleValidatorUseCase.isValidLactation(it)
    }

    val dob = MutableLiveData<Date>()

    val parent = MutableLiveData<String>()
    fun setParent(value: String?) { parent.value = value }

    val homeBorn = MutableLiveData<Boolean>()

    val purchaseAmount = MutableLiveData<String>()

    val purchaseDate = MutableLiveData<Date>()

    private val _selectParent = MutableLiveData<Event<String>>()
    val selectParent: LiveData<Event<String>>
        get() = _selectParent

    private val _showBackConfirmationDialog = MutableLiveData<Event<Unit>>()
    val showBackConfirmationDialog: LiveData<Event<Unit>>
        get() = _showBackConfirmationDialog

    private val _navigateUp = MediatorLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    private val _saving = MediatorLiveData<Boolean>().apply { value = false }
    val saving: LiveData<Boolean>
        get() = _saving

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val addUpdateCattleResult = MutableLiveData<Result<Unit>>()

    init {
        _tagNumberErrorMessage.addSource(tagNumber) {
            cattleValidatorUseCase.execute(Pair(it, oldCattle?.tagNumber))
        }

        _tagNumberErrorMessage.addSource(cattleValidatorUseCase.observe()) { result ->
            (result as? Success)?.data?.let {
                _tagNumberErrorMessage.value = it
            }
        }

        _navigateUp.addSource(addUpdateCattleResult) {
            (it as? Success)?.let {
                _navigateUp.value = Event(Unit)
            }
        }

        _showMessage.addSource(addUpdateCattleResult) {
            (it as? Error)?.exception?.let {
                _showMessage.value = Event(R.string.retry)
            }
        }

        _saving.addSource(addUpdateCattleResult) {
            _saving.value = false
        }
    }

    fun save() {
        if (isAllFieldsValid()) {
            val newCattle = getCattle()

            if (oldCattle != newCattle) {
                _saving.value = true

                if (oldCattle != null)
                    updateCattleUseCase(newCattle, addUpdateCattleResult)
                else
                    addCattleUseCase(newCattle, addUpdateCattleResult)

            } else {
                navigateUp()
            }
        } else {
            _showMessage.value = Event(R.string.error_fill_empty_fields)
        }
    }

    fun setCattle(cattle: Cattle) {
        oldCattle = cattle
        bindData(cattle)
    }

    private fun bindData(cattle: Cattle) {
        tagNumber.value = cattle.tagNumber.toString()
        name.value = cattle.name
        _type.value = cattle.type.displayName
        _breed.value = cattle.breed.displayName
        _group.value = cattle.group.displayName
        lactation.value = cattle.lactation.toString()
        dob.value = cattle.dateOfBirth
        parent.value = cattle.parent?.toString()
        homeBorn.value = cattle.homeBorn
        purchaseAmount.value = cattle.purchaseAmount?.toString()
        purchaseDate.value = cattle.purchaseDate
    }

    private fun getCattle(): Cattle =
        Cattle(
            tagNumber.value!!.toLong(),
            name.value,
            null,
            type.value!!.toType(),
            breed.value!!.toBreed(),
            group.value!!.toGroup(),
            lactation.value?.toInt() ?: 0,
            homeBorn.value ?: false,
            purchaseAmount.value?.toLongOrNull(),
            purchaseDate.value,
            dob.value,
            parent.value?.toLongOrNull()
        ).apply {
            oldCattle?.id?.let { id = it }
        }

    private fun isAllFieldsValid(): Boolean {
        return tagNumberErrorMessage.value == CattleValidatorUseCase.VALID_FIELD &&
                typeErrorMessage.value == CattleValidatorUseCase.VALID_FIELD &&
                breedErrorMessage.value == CattleValidatorUseCase.VALID_FIELD &&
                groupErrorMessage.value == CattleValidatorUseCase.VALID_FIELD &&
                lactationErrorMessage.value == CattleValidatorUseCase.VALID_FIELD
    }

    private fun isAllFieldsEmpty(): Boolean {
        return tagNumber.value.isNullOrEmpty() &&
                name.value.isNullOrEmpty() &&
                type.value.isNullOrEmpty() &&
                breed.value.isNullOrEmpty() &&
                group.value.isNullOrEmpty() &&
                lactation.value.isNullOrEmpty() &&
                dob.value == null &&
                parent.value.isNullOrEmpty() &&
                (homeBorn.value == null || homeBorn.value == false) &&
                purchaseAmount.value.isNullOrEmpty() &&
                purchaseDate.value == null
    }

    fun pickParent() = tagNumber.value.let {
        if (it != null)
            _selectParent.value = Event(it)
        else
            _showMessage.value = Event(R.string.provide_tag_number)
    }

    fun onBackPressed() {
        if (isAllFieldsEmpty())
            navigateUp()
        else
            _showBackConfirmationDialog.value = Event(Unit)
    }

    fun navigateUp() {
        _navigateUp.value = Event(Unit)
    }
}