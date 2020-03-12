package com.pr656d.cattlenotes.utils.databinding.twoway

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.utils.focus
import com.pr656d.cattlenotes.utils.hideKeyboard
import com.pr656d.cattlenotes.utils.pickADate
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalDate

/**
 * Custom two way data binding adapter for [LocalDate] used at [TextInputEditText] and
 * onClick : Opens date picker dialog by providing attribute value to true.
 * onLongClick : Opens dialog to remove text by providing attribute value to true.
 *
 * Example:
 *      app:dateText="@={ MutableLiveData<LocalDate>() }"
 *      app:onClickShowDatePicker="@{true}"
 *      app:onLongClickRemoveText="@{true}"
 */

@BindingAdapter("dateText")
fun setDate(view: TextInputEditText, newDate: LocalDate?) {
    view.text?.toString().let { text ->
        val date = if (!text.isNullOrEmpty())
            TimeUtils.toLocalDate(text)
        else
            null

        if (date != newDate) {
            view.setText(
                newDate?.let {
                    TimeUtils.dateString(it)
                }
            )
        }
    }
}

@InverseBindingAdapter(attribute = "dateText")
fun getDate(view: TextInputEditText): LocalDate? {
    view.text?.toString()?.let {
        if (it.isNotEmpty())
            return TimeUtils.toLocalDate(it)
    }
    return null
}

@BindingAdapter(
    value = [
        "onClickShowDatePicker",
        "onClickListener",
        "onLongClickRemoveText",
        "onLongClickListener",
        "dateTextAttrChanged"
    ], requireAll = false
)
fun setListeners(
    view: TextInputEditText,
    onClickShowDatePicker: Boolean = false,
    onClickListener: View.OnClickListener?,
    onLongClickRemoveText: Boolean = false,
    onLongClickListener: View.OnLongClickListener?,
    attrChange: InverseBindingListener?
) {
    val context = view.context

    if (attrChange == null) {
        view.setOnClickListener(onClickListener)
        view.setOnLongClickListener(onLongClickListener)
    } else {
        if (onClickShowDatePicker)
            view.setOnClickListener { v ->
                onClickListener?.onClick(v)

                // Hide keyboard if visible.
                hideKeyboard(v)
                // Take view in focus.
                view.focus()
                // Show dialog.
                v.pickADate(
                    onDateCancelled = {
                        view.isFocusableInTouchMode = false
                    },
                    onDateSet = { _, dd, mm, yyyy ->
                        TimeUtils.toLocalDate(dd, mm, yyyy).let {
                            view.setText(TimeUtils.dateString(it))
                            view.isFocusableInTouchMode = false
                            attrChange.onChange()
                        }
                    }
                )
        }

        if (onLongClickRemoveText)
            view.setOnLongClickListener { v ->
                // Hide keyboard if visible.
                hideKeyboard(v)
                // Take view in focus.
                view.focus()

                if (!view.text.isNullOrEmpty()) {
                    // Show dialog.
                    MaterialAlertDialogBuilder(context)
                        .setMessage(context.getString(R.string.remove_text, view.hint.toString()))
                        .setPositiveButton(R.string.yes) { _, _ ->
                            view.text = null
                            view.isFocusableInTouchMode = false
                            attrChange.onChange()
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            view.isFocusableInTouchMode = false
                        }
                        .create()
                        .show()
                }
                true
            }
    }
}