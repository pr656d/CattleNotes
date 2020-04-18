package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.cattle.addedit.parent.ParentActionListener
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.addedit.AddCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.UpdateCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.parent.GetParentListUseCase
import com.pr656d.shared.domain.cattle.validator.CattleTagNumberValidatorUseCase
import com.pr656d.shared.domain.cattle.validator.CattleValidator
import com.pr656d.shared.domain.cattle.validator.CattleValidator.VALID_FIELD
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.toBreed
import com.pr656d.shared.utils.toGroup
import com.pr656d.shared.utils.toType
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AddEditCattleViewModel @Inject constructor(
    private val addCattleUseCase: AddCattleUseCase,
    private val updateCattleUseCase: UpdateCattleUseCase,
    private val getParentListUseCase: GetParentListUseCase,
    cattleTagNumberValidatorUseCase: CattleTagNumberValidatorUseCase
) : ViewModel(), ParentActionListener {
    private var oldCattle: Cattle? = null

    private val _editing = MutableLiveData<Boolean>(false)
    val editing: LiveData<Boolean>
        get() = _editing

    val tagNumber = MutableLiveData<String>()
    private val _tagNumberErrorMessage = MediatorLiveData<@StringRes Int>()
    val tagNumberErrorMessage: LiveData<Int>
        get() = _tagNumberErrorMessage

    val name = MutableLiveData<String>()

    val type = MutableLiveData<String>()
    val typeErrorMessage: LiveData<Int> = type.map {
        CattleValidator.isValidType(it)
    }

    val breed = MutableLiveData<String>()
    val breedErrorMessage: LiveData<Int> = breed.map {
        CattleValidator.isValidBreed(it)
    }

    val group = MutableLiveData<String>()
    val groupErrorMessage: LiveData<Int> = group.map {
        CattleValidator.isValidGroup(it)
    }

    val lactation = MutableLiveData<String>()
    val lactationErrorMessage: LiveData<Int> = lactation.map {
        CattleValidator.isValidLactation(it)
    }

    val dob = MutableLiveData<LocalDate>()

    val parent = MutableLiveData<String>()

    val homeBorn = MutableLiveData<Boolean>()

    val purchaseAmount = MutableLiveData<String>()

    val purchaseDate = MutableLiveData<LocalDate>()

    private val _selectingParent = MutableLiveData<Boolean>(false)
    val selectingParent: LiveData<Boolean>
        get() = _selectingParent

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

    private val _parentList = MediatorLiveData<List<Cattle>>()
    val parentList: LiveData<List<Cattle>>
        get() = _parentList

    private val _loadingParentList = MutableLiveData<Boolean>()
    val loadingParentList: LiveData<Boolean>
        get() = _loadingParentList

    private val _isEmptyParentList = MediatorLiveData<Boolean>()
    val isEmptyParentList: LiveData<Boolean>
        get() = _isEmptyParentList

    private val parentListResult = getParentListUseCase.observe()

    private val addUpdateCattleResult = MutableLiveData<Result<Unit>>()

    init {
        _tagNumberErrorMessage.addSource(tagNumber) {
            cattleTagNumberValidatorUseCase.execute(Pair(it, oldCattle?.tagNumber))
        }

        _tagNumberErrorMessage.addSource(cattleTagNumberValidatorUseCase.observe()) { result ->
            (result as? Success)?.data?.let {
                _tagNumberErrorMessage.value = it
            }
        }

        _navigateUp.addSource(addUpdateCattleResult) {
            (it as? Success)?.let {
                navigateUp()
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

        _parentList.addSource(tagNumber) {
            it.toLongOrNull()?.let { tagNumber ->
                _loadingParentList.postValue(true)
                getParentListUseCase.execute(tagNumber)
            }
        }

        _isEmptyParentList.addSource(parentList) {
            _isEmptyParentList.value = it.isNullOrEmpty()
        }

        _parentList.addSource(parentListResult) {
            (it as? Success)?.data?.let { list ->
                _parentList.postValue(list)
                _loadingParentList.postValue(false)
            }
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
        _editing.postValue(true)
        oldCattle = cattle
        bindData(cattle)
    }

    private fun bindData(cattle: Cattle) {
        tagNumber.value = cattle.tagNumber.toString()
        name.value = cattle.name
        type.value = cattle.type.displayName
        breed.value = cattle.breed.displayName
        group.value = cattle.group.displayName
        lactation.value = cattle.lactation.toString()
        dob.value = cattle.dateOfBirth
        parent.value = cattle.parent
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
            lactation.value?.toLongOrNull() ?: 0,
            homeBorn.value ?: false,
            purchaseAmount.value?.toLongOrNull(),
            purchaseDate.value,
            dob.value,
            parent.value
        ).apply {
            id = oldCattle?.id ?: FirestoreUtil.autoId()
        }

    private fun isAllFieldsValid(): Boolean {
        return tagNumberErrorMessage.value == VALID_FIELD &&
                typeErrorMessage.value == VALID_FIELD &&
                breedErrorMessage.value == VALID_FIELD &&
                groupErrorMessage.value == VALID_FIELD &&
                lactationErrorMessage.value == VALID_FIELD
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

    override fun parentSelected(cattle: Cattle) {
        parent.postValue(cattle.id)
        _selectingParent.postValue(false)
    }

    fun pickParent() = tagNumber.value.let {
        if (tagNumberErrorMessage.value != VALID_FIELD) {
            _showMessage.value = Event(R.string.error_provide_valid_tag_number)
            return
        }
        if (it != null) {
            if (!parentList.value.isNullOrEmpty())
                _selectingParent.postValue(true)
            else
                _showMessage.value = Event(R.string.parent_list_empty)
        } else {
            _showMessage.value = Event(R.string.error_provide_tag_number)
        }
    }

    fun onBackPressed(backConfirmation: Boolean = false) {
        when {
            isAllFieldsEmpty() -> navigateUp()
            backConfirmation -> navigateUp()
            !backConfirmation -> _showBackConfirmationDialog.value = Event(Unit)
        }
    }

    private fun navigateUp() {
        _navigateUp.value = Event(Unit)
    }
}