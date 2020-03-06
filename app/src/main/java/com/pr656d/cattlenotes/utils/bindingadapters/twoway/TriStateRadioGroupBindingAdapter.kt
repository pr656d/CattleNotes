package com.pr656d.cattlenotes.utils.bindingadapters.twoway

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