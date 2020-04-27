package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.cattle.addedit.parent.ParentActionListener
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.addedit.AddCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.UpdateCattleUseCase
import com.pr656d.shared.domain.cattle.addedit.parent.GetParentListUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetParentCattleUseCase
import com.pr656d.shared.domain.cattle.validator.CattleTagNumberValidatorUseCase
import com.pr656d.shared.domain.cattle.validator.CattleValidator
import com.pr656d.shared.domain.cattle.validator.CattleValidator.VALID_FIELD
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.nameOrTagNumber
import com.pr656d.shared.utils.toGroup
import com.pr656d.shared.utils.toType
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AddEditCattleViewModel @Inject constructor(
    private val addCattleUseCase: AddCattleUseCase,
    private val updateCattleUseCase: UpdateCattleUseCase,
    private val getParentListUseCase: GetParentListUseCase,
    getCattleByIdUseCase: GetCattleByIdUseCase,
    getParentCattleUseCase: GetParentCattleUseCase,
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
    val typeList: LiveData<Int> = MutableLiveData(R.array.list_type)

    val isCattleTypeBull: LiveData<Boolean> = type.map { it.toType() is AnimalType.Bull }

    val breed = MediatorLiveData<String>()
    val breedErrorMessage: LiveData<Int> = breed.map {
        CattleValidator.isValidBreed(it)
    }
    val breedList: LiveData<Int> = type.map {
        // Reset breed.
        breed.postValue(null)

        // Check if it's same as old cattle.
        if (oldCattle.value?.type?.displayName == it)
            breed.postValue(oldCattle.value?.breed)

        when (it.toType()) {
            AnimalType.Cow -> R.array.list_breed_cow
            AnimalType.Buffalo -> R.array.list_breed_buffalo
            AnimalType.Bull -> R.array.list_breed_bull
        }
    }

    val group = MediatorLiveData<String?>()
    val groupErrorMessage: LiveData<Int> = group.map {
        CattleValidator.isValidGroup(it, type.value)
    }

    val lactation = MediatorLiveData<String>()
    val lactationErrorMessage: LiveData<Int> = lactation.map {
        CattleValidator.isValidLactation(it, type.value)
    }

    val dob = MediatorLiveData<LocalDate>()

    private val parentId = MediatorLiveData<String?>()

    val parentCattle: LiveData<Cattle?> = parentId.switchMap { id ->
        id?.let { getParentCattleUseCase(id) } ?: MutableLiveData<Cattle?>(null)
    }

    val parent:LiveData<String?> = parentCattle.map { it?.nameOrTagNumber() }

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
            type.postValue(it?.type?.displayName)
        }

        breed.addSource(oldCattle) {
            breed.postValue(it?.breed)
        }

        group.addSource(oldCattle) {
            group.postValue(it?.group?.displayName)
        }

        group.addSource(isCattleTypeBull) {
            if (it) {
                // Bull doesn't have group
                group.postValue(null)
            } else {
                group.postValue(oldCattle.value?.group?.displayName)
            }
        }

        lactation.addSource(oldCattle) {
            lactation.postValue(it?.lactation?.toString())
        }

        lactation.addSource(isCattleTypeBull) {
            if (it) {
                // Bull doesn't have group
                lactation.postValue(null)
            } else {
                lactation.postValue(oldCattle.value?.lactation?.toString())
            }
        }

        dob.addSource(oldCattle) {
            dob.postValue(it?.dateOfBirth)
        }

        parentId.addSource(oldCattle) {
            parentId.postValue(it?.parent)
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
            breed.value!!,
            group.value?.toGroup(),
            lactation.value?.toLongOrNull(),
            homeBorn.value ?: false,
            purchaseAmount.value?.toLongOrNull(),
            purchaseDate.value,
            dob.value,
            parentId.value
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

    override fun parentSelected(parent: Cattle) {
        parentId.postValue(parent.id)
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