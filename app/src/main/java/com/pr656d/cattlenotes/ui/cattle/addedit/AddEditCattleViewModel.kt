package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.cattle.addedit.parent.ParentActionListener
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.addedit.AddCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.UpdateCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.parent.GetParentListUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
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
    getCattleByIdUseCase: GetCattleByIdUseCase,
    cattleTagNumberValidatorUseCase: CattleTagNumberValidatorUseCase
) : ViewModel(), ParentActionListener {
    private val cattleId = MutableLiveData<String>()

    // Make it open to test
    val oldCattle: LiveData<Cattle?> = cattleId.switchMap { getCattleByIdUseCase(it) }

    private val _editing = MutableLiveData<Boolean>(false)
    val editing: LiveData<Boolean>
        get() = _editing

    val tagNumber = MediatorLiveData<String>()
    private val _tagNumberErrorMessage = MediatorLiveData<@StringRes Int>()
    val tagNumberErrorMessage: LiveData<Int>
        get() = _tagNumberErrorMessage

    val name = MediatorLiveData<String>()

    val type = MediatorLiveData<String>()
    val typeErrorMessage: LiveData<Int> = type.map {
        CattleValidator.isValidType(it)
    }

    val breed = MediatorLiveData<String>()
    val breedErrorMessage: LiveData<Int> = breed.map {
        CattleValidator.isValidBreed(it)
    }

    val group = MediatorLiveData<String>()
    val groupErrorMessage: LiveData<Int> = group.map {
        CattleValidator.isValidGroup(it)
    }

    val lactation = MediatorLiveData<String>()
    val lactationErrorMessage: LiveData<Int> = lactation.map {
        CattleValidator.isValidLactation(it)
    }

    val dob = MediatorLiveData<LocalDate>()

    val parent = MediatorLiveData<String>()

    val homeBorn = MediatorLiveData<Boolean>()

    val purchaseAmount = MediatorLiveData<String>()

    val purchaseDate = MediatorLiveData<LocalDate>()

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
        tagNumber.addSource(oldCattle) {
            tagNumber.postValue(it?.tagNumber?.toString())
        }

        name.addSource(oldCattle) {
            name.postValue(it?.name)
        }

        type.addSource(oldCattle) {
            type.postValue(it?.type?.toString())
        }

        breed.addSource(oldCattle) {
            breed.postValue(it?.breed?.displayName)
        }

        group.addSource(oldCattle) {
            group.postValue(it?.group?.displayName)
        }

        lactation.addSource(oldCattle) {
            lactation.postValue(it?.lactation?.toString())
        }

        dob.addSource(oldCattle) {
            dob.postValue(it?.dateOfBirth)
        }

        parent.addSource(oldCattle) {
            parent.postValue(it?.parent)
        }

        homeBorn.addSource(oldCattle) {
            homeBorn.postValue(it?.homeBorn)
        }

        purchaseAmount.addSource(oldCattle) {
            purchaseAmount.postValue(it?.purchaseAmount?.toString())
        }

        purchaseDate.addSource(oldCattle) {
            purchaseDate.postValue(it?.purchaseDate)
        }

        _tagNumberErrorMessage.addSource(tagNumber) {
            cattleTagNumberValidatorUseCase.execute(it to oldCattle.value?.tagNumber)
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

            if (oldCattle.value != newCattle) {
                _saving.value = true

                if (oldCattle.value != null)
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

    fun setCattle(id: String) {
        _editing.postValue(true)
        cattleId.postValue(id)
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
            id = oldCattle.value?.id ?: FirestoreUtil.autoId()
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