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
package com.pr656d.cattlenotes.ui.milking.add

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.R
import com.pr656d.model.Milk
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.validator.MilkValidator
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.succeeded
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.toMilkOf
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class AddMilkViewModel @Inject constructor(
    private val addMilkUseCase: AddMilkUseCase
) : ViewModel() {

    val dateTime = MutableLiveData<ZonedDateTime>()
    val dateTimeErrorMessage: LiveData<Int> = dateTime.map {
        MilkValidator.isDateTimeValid(it)
    }

    val milkOf = MutableLiveData<String>()
    val milkOfErrorMessage: LiveData<Int> = milkOf.map {
        MilkValidator.isValidType(it)
    }
    val milkOfList: LiveData<Int> = MutableLiveData(R.array.list_milk_of)

    val quantity = MutableLiveData<String>()
    val quantityErrorMessage: LiveData<Int> = quantity.map {
        MilkValidator.isQuantityValid(it)
    }

    val fat = MutableLiveData<String>()
    val fatErrorMessage: LiveData<Int> = fat.map {
        MilkValidator.isFatValid(it)
    }

    val amount = MutableLiveData<String>()

    private val _dismiss = MutableLiveData<Event<Unit>>()
    val dismiss: LiveData<Event<Unit>>
        get() = _dismiss

    private val _showMessage = MutableLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    fun saveMilk() {
        if (isAllFieldsValid()) {
            viewModelScope.launch {
                val result = addMilkUseCase(getMilk())
                if (result.succeeded)
                    dismiss()
                else
                    showMessage(R.string.error_add_milk)
            }
        } else {
            showMessage(R.string.error_fill_empty_fields)
        }
    }

    fun dismiss() {
        _dismiss.postValue(Event(Unit))
    }

    private fun showMessage(@StringRes messageId: Int) {
        _showMessage.postValue(Event(messageId))
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
