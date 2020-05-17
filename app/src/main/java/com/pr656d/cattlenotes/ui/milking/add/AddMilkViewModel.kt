/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.milking.add

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.model.Milk
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.validator.MilkValidator
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.toMilkOf
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class AddMilkViewModel @Inject constructor(
    private val addMilkUseCase: AddMilkUseCase
) : ViewModel() {

    private val addMilkResult = MutableLiveData<Result<Unit>>()

    val dateTime = MediatorLiveData<ZonedDateTime>()
    val dateTimeErrorMessage: LiveData<Int> = dateTime.map {
        MilkValidator.isDateTimeValid(it)
    }

    val milkOf = MediatorLiveData<String>()
    val milkOfErrorMessage: LiveData<Int> = milkOf.map {
        MilkValidator.isValidType(it)
    }
    val milkOfList: LiveData<Int> = MutableLiveData(R.array.list_milk_of)

    val quantity = MediatorLiveData<String>()
    val quantityErrorMessage: LiveData<Int> = quantity.map {
        MilkValidator.isQuantityValid(it)
    }

    val fat = MediatorLiveData<String>()
    val fatErrorMessage: LiveData<Int> = fat.map {
        MilkValidator.isFatValid(it)
    }

    val amount = MediatorLiveData<String>()

    private val _dismiss = MediatorLiveData<Event<Unit>>()
    val dismiss: LiveData<Event<Unit>>
        get() = _dismiss

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    init {
        _dismiss.addSource(addMilkResult) {
            (it as? Result.Success)?.data?.let {
                dismiss()
            }
        }

        _showMessage.addSource(addMilkResult) {
            (it as? Result.Error)?.exception?.let {
                showMessage(R.string.error_add_milk)
            }
        }
    }

    fun saveMilk() {
        if (isAllFieldsValid()) {
            val newMilk = getMilk()
            addMilkUseCase(newMilk, addMilkResult)
        } else {
            showMessage(R.string.error_fill_empty_fields)
        }
    }

    fun dismiss() {
        _dismiss.value = Event(Unit)
    }

    private fun showMessage(@StringRes messageId: Int) {
        _showMessage.value = Event(messageId)
    }

    private fun getMilk(): Milk = Milk(
        Milk.Source.Manual,
        dateTime.value!!,
        milkOf.value!!.toMilkOf(),
        quantity.value!!.toFloat(),
        fat.value!!.toFloat(),
        amount.value?.toFloatOrNull()
    ).apply { id = FirestoreUtil.autoId() }

    private fun isAllFieldsValid(): Boolean {
        return dateTimeErrorMessage.value == MilkValidator.VALID_FIELD &&
                quantityErrorMessage.value == MilkValidator.VALID_FIELD &&
                fatErrorMessage.value == MilkValidator.VALID_FIELD &&
                milkOfErrorMessage.value == MilkValidator.VALID_FIELD
    }
}