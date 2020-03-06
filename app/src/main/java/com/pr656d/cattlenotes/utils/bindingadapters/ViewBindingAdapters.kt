package com.pr656d.cattlenotes.utils.bindingadapters

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Set text on a [TextView] from a string resource. */
@BindingAdapter("android:text")
fun setText(view: TextView, @StringRes resId: Int) {
    view.apply {
        if (resId == 0)
            text = null
        else
            setText(resId)
    }
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) VISIBLE else GONE
}

/** Removes text if boolean is true. */
@BindingAdapter("resetText")
fun resetText(view: TextInputEditText, value: Boolean) {
    if (value) view.text = null
}

/** Set [errorText] on [TextInputLayout] from string resource. */
@BindingAdapter("errorText")
fun errorText(view: TextInputLayout, @StringRes messageId: Int) {
    view.apply {
        if (messageId == 0) {
            isErrorEnabled = false
            error = null
        } else {
            isErrorEnabled = true
            error = context.getString(messageId)
        }
    }
}