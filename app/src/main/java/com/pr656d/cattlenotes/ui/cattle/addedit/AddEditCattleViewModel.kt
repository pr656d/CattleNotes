/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.cattle.addedit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
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
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.domain.result.succeeded
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.domain.result.updateOnSuccess
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.nameOrTagNumber
import com.pr656d.shared.utils.toGroup
import com.pr656d.shared.utils.toType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AddEditCattleViewModel @Inject constructor(
    private val addCattleUseCase: AddCattleUseCase,
    private val updateCattleUseCase: UpdateCattleUseCase,
    private val getParentListUseCase: GetParentListUseCase,
    private val getCattleByIdUseCase: GetCattleByIdUseCase,
    getParentCattleUseCase: GetParentCattleUseCase,
    private val cattleTagNumberValidatorUseCase: CattleTagNumberValidatorUseCase
) : ViewModel(), ParentActionListener {
    private val cattleId = MutableLiveData<String>()

    // Make it open to test
    val oldCattle: LiveData<Cattle?> = cattleId.switchMap { id ->
        _loading.postValue(true)

        getCattleByIdUseCase(id)
            .map {
                _loading.postValue(false)
                it.successOr(null)
            }
            .asLiveData()
    }

    private val _editing = MutableLiveData(false)
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

        it.toType().getBreedList()
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

    // Make it open to test
    val parentCattle = MediatorLiveData<Cattle?>()

    val parent: LiveData<String?> = parentCattle.map { it?.nameOrTagNumber() }

    val homeBorn = MediatorLiveData<Boolean>()

    val purchaseAmount = MediatorLiveData<String>()

    val purchaseDate = MediatorLiveData<LocalDate>()

    private val _selectingParent = MutableLiveData(false)
    val selectingParent: LiveData<Boolean>
        get() = _selectingParent

    private val _showBackConfirmationDialog = MutableLiveData<Event<Unit>>()
    val showBackConfirmationDialog: LiveData<Event<Unit>>
        get() = _showBackConfirmationDialog

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

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

        parentCattle.addSource(oldCattle) {
            it ?: return@addSource

            viewModelScope.launch {
                getParentCattleUseCase(it).updateOnSuccess(parentCattle)
            }
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
            viewModelScope.launch {
                cattleTagNumberValidatorUseCase(it to oldCattle.value?.tagNumber)
                    .updateOnSuccess(_tagNumberErrorMessage)
            }
        }

        _parentList.addSource(tagNumber) {
            it.toLongOrNull()?.let { tagNumber ->
                viewModelScope.launch {
                    _loadingParentList.postValue(true)

                    getParentListUseCase(tagNumber).let { result ->
                        _parentList.postValue((result as? Success)?.data)
                    }

                    _loadingParentList.postValue(false)
                }
            }
        }

        _isEmptyParentList.addSource(parentList) {
            _isEmptyParentList.value = it.isNullOrEmpty()
        }
    }

    fun save() {
        if (isAllFieldsValid()) {
            val newCattle = getCattle()

            if (oldCattle.value != newCattle) {
                viewModelScope.launch {
                    _loading.postValue(true)

                    val result = if (oldCattle.value != null)
                        updateCattleUseCase(newCattle)
                    else
                        addCattleUseCase(newCattle)

                    if (result.succeeded) {
                        navigateUp()
                    } else {
                        _showMessage.postValue(Event(R.string.retry))
                    }

                    _loading.postValue(false)
                }
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

    fun setParent(id: String) {
        viewModelScope.launch {
            getCattleByIdUseCase(id)
                .firstOrNull()
                ?.updateOnSuccess(parentCattle)
        }
    }

    private fun getCattle(): Cattle =
        Cattle(
            oldCattle.value?.id ?: FirestoreUtil.autoId(),
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
            parentCattle.value?.id
        )

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
        parentCattle.postValue(cattle)
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
        _navigateUp.postValue(Event(Unit))
    }

    @StringRes
    private fun AnimalType.getBreedList(): Int = when (this) {
        AnimalType.Cow -> R.array.list_breed_cow
        AnimalType.Buffalo -> R.array.list_breed_buffalo
        AnimalType.Bull -> R.array.list_breed_bull
    }
}
