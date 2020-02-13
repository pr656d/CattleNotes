package com.pr656d.cattlenotes.ui.main.cattle.addedit

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleViewModel.Destination.DESTINATIONS.*
import com.pr656d.cattlenotes.utils.Event
import com.pr656d.cattlenotes.utils.toBreed
import com.pr656d.cattlenotes.utils.toGroup
import com.pr656d.cattlenotes.utils.toType
import com.pr656d.cattlenotes.utils.validator.CattleValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class AddEditCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : ViewModel() {

    private var oldCattle: Cattle? = null

    val tagNumber = MutableLiveData<String>()
    val tagNumberErrorMessage: LiveData<Int> = tagNumber.map {
        CattleValidator.isValidTagNumber(it.toLongOrNull(), oldCattle)
    }

    val name = MutableLiveData<String?>()

    private val _type = MutableLiveData<String>()
    fun setType(text: String?) { _type.value = text }
    val type: LiveData<String>
        get() = _type
    val typeErrorMessage: LiveData<Int> = type.map { CattleValidator.isValidType(it) }

    private val _breed = MutableLiveData<String>()
    fun setBreed(text: String?) { _breed.value = text }
    val breed: LiveData<String>
        get() = _breed
    val breedErrorMessage: LiveData<Int> = _breed.map { CattleValidator.isValidBreed(it) }

    private val _group = MutableLiveData<String>()
    fun setGroup(text: String?) { _group.value = text }
    val group: LiveData<String>
        get() = _group
    val groupErrorMessage: LiveData<Int> = _group.map { CattleValidator.isValidGroup(it) }

    val lactation = MutableLiveData<String>()
    val lactationErrorMessage: LiveData<Int> = lactation.map { CattleValidator.isValidLactation(it) }

    val dob = MutableLiveData<Date>()

    val parent = MutableLiveData<String>()
    fun setParent(value: String?) { parent.value = value }

    val homeBorn = MutableLiveData<Boolean>()

    val purchaseAmount = MutableLiveData<String>()

    val purchaseDate = MutableLiveData<Date>()

    private val _action = MutableLiveData<Event<Destination>>()
    val action: LiveData<Event<Destination>> = _action

    private val _saving = MutableLiveData<Boolean>(false)
    val saving: LiveData<Boolean> = _saving

    private val _showMessage = MutableLiveData<@StringRes Int>()
    val showMessage: LiveData<Event<Int>> = _showMessage.map { Event(it) }

    fun save() {
        val toggleSaving: suspend () -> Unit = {
            withContext(Dispatchers.Main) {
                _saving.value = _saving.value!!.not()
            }
        }

        if (isAllFieldsValid()) {
            val newCattle = getCattle()

            if (oldCattle != newCattle) {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        toggleSaving()

                        if (oldCattle != null) {
                            cattleDataRepository.updateCattle(newCattle)
                        } else {
                            cattleDataRepository.addCattle(newCattle)

                        }

                        toggleSaving()

                        _action.postValue(Event(Destination(NAVIGATE_UP)))
                    } catch (e: Exception) {
                        toggleSaving()
                        _showMessage.postValue(R.string.retry)
                    }
                }
            } else {
                _action.value = Event(Destination(NAVIGATE_UP))
            }
        } else {
            _showMessage.value = R.string.error_fill_empty_fields
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
        return tagNumberErrorMessage.value == CattleValidator.VALID_FIELD &&
                typeErrorMessage.value == CattleValidator.VALID_FIELD &&
                breedErrorMessage.value == CattleValidator.VALID_FIELD &&
                groupErrorMessage.value == CattleValidator.VALID_FIELD &&
                lactationErrorMessage.value == CattleValidator.VALID_FIELD
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

    fun pickParent() {
        tagNumber.value?.let {
            _action.value = Event(Destination(PICK_PARENT, it))
            return
        }
        _showMessage.value = R.string.provide_tag_number
    }

    fun removeParent(view: View): Boolean {
        _action.value = Event(Destination(REMOVE_PARENT))
        return true
    }

    fun showAllBreeding() {
        tagNumber.value?.let {
            _action.value = Event(Destination(ALL_BREEDING_SCREEN, it))
            return
        }
        _showMessage.value = R.string.provide_tag_number
    }

    fun addNewBreeding() {
        if (isAllFieldsValid()) {
            _action.value = Event(Destination(ADD_BREEDING_SCREEN, Gson().toJson(getCattle())))
        } else {
            _showMessage.value = R.string.error_fill_empty_fields
        }
    }

    fun showActiveBreeding() {
        tagNumber.value?.let {
            _action.value = Event(Destination(ACTIVE_BREEDING, it))
            return
        }
        _showMessage.value = R.string.provide_tag_number
    }

    fun onBackPressed() {
        if (isAllFieldsEmpty())
            navigateUp()
        else
            _action.value = Event(Destination(BACK_CONFIRMATION_DIALOG))
    }

    fun navigateUp() {
        _action.value = Event(Destination(NAVIGATE_UP))
    }

    data class Destination(val destination: DESTINATIONS, val data: String? = null) {
        enum class DESTINATIONS {
            PICK_PARENT, REMOVE_PARENT,
            ACTIVE_BREEDING, ALL_BREEDING_SCREEN, ADD_BREEDING_SCREEN,
            BACK_CONFIRMATION_DIALOG, NAVIGATE_UP
        }
    }
}