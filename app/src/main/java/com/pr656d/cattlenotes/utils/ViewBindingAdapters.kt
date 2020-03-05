package com.pr656d.cattlenotes.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import java.util.*

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

/** Set text on [TextInputEditText] from [Date] type.  */
@BindingAdapter("dateText")
fun dateText(view: TextInputEditText, value: Date?) {
    if (value == null) {
        view.text = null
    } else {
        val text = view.context.getString(value)
        view.setText(text)
    }
}

/** onLongClick listener on [MutableLiveData] to remove text by showing confirmation dialog */
@BindingAdapter("onLongClickRemoveText")
fun onLongClickRemoveText(view: TextInputEditText, data: MutableLiveData<*>?) {
    view.setOnLongClickListener {
        // Hide keyboard if visible.
        it.context.hideKeyboard(it)
        // Take view in focus.
        view.focus()

        // Show dialog.
        view.context.showDialog(
            title = R.string.remove_text,
            positiveTextId = R.string.yes,
            onPositiveSelected = {
                data?.value = null
                view.text = null
                view.isFocusableInTouchMode = false
            },
            negativeTextId = R.string.no,
            onNegativeSelected = {
                view.isFocusableInTouchMode = false
            }
        )
        true
    }
}

/** onClick listener on [MutableLiveData] to show date picker dialog and set value */
@BindingAdapter("onClickPickDate")
fun onClickPickDate(view: TextInputEditText, data: MutableLiveData<Date>?) {
    view.setOnClickListener {
        // Hide keyboard if visible.
        it.context.hideKeyboard(it)
        // Take view in focus.
        view.focus()

        // Show dialog.
        it.pickADate(
            onDateCancelled = {
                view.isFocusableInTouchMode = false
            },
            onDateSet = { _, dd, mm, yyyy ->
                it.context.toDate(dd, mm, yyyy)?.let { date ->
                    data?.value = date
                    view.setText(it.context.getString(date))
                    view.isFocusableInTouchMode = false
                }
            })
    }
}