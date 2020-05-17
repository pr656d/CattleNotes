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

package com.pr656d.cattlenotes.utils.databinding.twoway

import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.pr656d.cattlenotes.R

/**
 * Custom two way data binding adapter which takes nullable [Boolean] and sets [RadioButton] accordingly.
 * Radio group represents tri state of options.
 * 1. Negative (false)
 * 2. Positive (true)
 * 3. None (null)
 */

@BindingAdapter("checkedButton")
fun setCheckedButton(radioGroup: RadioGroup, state: Boolean?) {
    val checkedButtonId = when (state) {
        null -> R.id.radioButtonNeutral
        true -> R.id.radioButtonPositive
        false -> R.id.radioButtonNegative
    }
    if (radioGroup.checkedRadioButtonId != checkedButtonId)
        radioGroup.check(checkedButtonId)
}

@InverseBindingAdapter(attribute = "checkedButton")
fun getCheckedButton(radioGroup: RadioGroup): Boolean? = when (radioGroup.checkedRadioButtonId) {
    R.id.radioButtonNeutral -> null
    R.id.radioButtonPositive -> true
    R.id.radioButtonNegative -> false
    else -> throw IllegalArgumentException("Invalid checkedRadioButtonId")
}

@BindingAdapter(value = ["onCheckedChanged", "checkedButtonAttrChanged"], requireAll = false)
fun setOnCheckedChangeListener(
    radioGroup: RadioGroup,
    listener: RadioGroup.OnCheckedChangeListener?,
    attrChange: InverseBindingListener?
) {
    if (attrChange == null) {
        radioGroup.setOnCheckedChangeListener(listener)
    } else {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            listener?.onCheckedChanged(group, checkedId)
            attrChange.onChange()
        }
    }
}