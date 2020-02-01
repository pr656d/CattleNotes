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

/**
 * Set [errorText] on [TextInputLayout] from string resource.
 */
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

/** Set text on [TextInputEditText] from [Date] type.  */
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

/**
 * Set text on [AutoCompleteTextView] which is used in [TextInputLayout] for dropdown menu.
 * Default [AutoCompleteTextView.setText] method filters so dropdown doesn't show all options
 * when pre set option is needed.
 * pass false for filter while setting text which will not filter and shows all options of dropdown.
 */
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
