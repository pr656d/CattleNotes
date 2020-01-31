package com.pr656d.cattlenotes.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import java.util.*

/** Set text on a [TextView] from a string resource. */
@BindingAdapter("android:text")
fun setText(view: TextView, @StringRes resId: Int) {
    if (resId == 0) {
        view.text = null
    } else {
        view.setText(resId)
    }
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("errorText")
fun errorText(view: TextInputLayout, @StringRes message: Int) {
    if (message == 0) {
        view.isErrorEnabled = false
    } else {
        view.isErrorEnabled = true
        view.error = view.context.getString(message)
    }
}

@BindingAdapter("helperText")
fun helperText(view: TextInputLayout, value: String?) {
    if (value.isNullOrBlank()) {
        view.isHelperTextEnabled = true
        view.helperText = view.context.getString(R.string.required)
    } else {
        view.isHelperTextEnabled = false
    }
}

@BindingAdapter("dateText")
fun dateText(view: TextInputEditText, value: Date?) {
    with(view) {
        if (value == null) {
            view.text = null
        } else {
            setText(context.getString(value))
        }
    }
}

@BindingAdapter("dropDownText")
fun dropDownText(view: AutoCompleteTextView, text: String?) {
    with(view) {
        if (text.isNullOrEmpty()) {
            setText(null, false)
        } else {
            setText(text, false)
        }
    }
}
