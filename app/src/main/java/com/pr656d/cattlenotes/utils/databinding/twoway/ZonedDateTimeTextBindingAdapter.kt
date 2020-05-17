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

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.utils.focus
import com.pr656d.cattlenotes.utils.hideKeyboard
import com.pr656d.cattlenotes.utils.pickADateTime
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.ZonedDateTime

/**
 * Custom two way data binding adapter for [ZonedDateTime] used at [TextInputEditText] and
 * onClick : Opens date time picker dialog by providing attribute value to true.
 * onLongClick : Opens dialog to remove text by providing attribute value to true.
 *
 * Example:
 *      app:dateText="@={ MutableLiveData<ZonedDateTime>() }"
 *      app:onClickShowDateTimePicker="@{true}"
 *      app:onLongClickRemoveText="@{true}"
 */

@BindingAdapter("dateTimeText")
fun setDateTime(view: TextInputEditText, newDateTime: ZonedDateTime?) {
    view.text?.toString().let { text ->
        val date = if (!text.isNullOrEmpty())
            TimeUtils.toZonedDateTime(text)
        else
            null

        if (date != newDateTime) {
            view.setText(
                newDateTime?.let {
                    TimeUtils.dateTimeString(it)
                }
            )
        }
    }
}

@InverseBindingAdapter(attribute = "dateTimeText")
fun getDateTime(view: TextInputEditText): ZonedDateTime? {
    view.text?.toString().let {
        if (!it.isNullOrBlank())
            return TimeUtils.toZonedDateTime(it)
    }
    return null
}

@BindingAdapter(
    value = [
        "onClickShowDateTimePicker",
        "onClickListener",
        "onLongClickRemoveText",
        "onLongClickListener",
        "dateTimeTextAttrChanged"
    ], requireAll = false
)
fun setDateTimeListeners(
    view: TextInputEditText,
    onClickShowDateTimePicker: Boolean = false,
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
        if (onClickShowDateTimePicker)
            view.setOnClickListener { v ->
                onClickListener?.onClick(v)

                // Hide keyboard if visible.
                hideKeyboard(v)
                // Take view in focus.
                view.focus()

                // Show dialog.
                v.pickADateTime(
                    onDateTimeCancelled = {
                        view.isFocusableInTouchMode = false
                    },
                    onDateTimeSet = { _, dd, mm, yyyy, hour, minute ->
                        // `Month + 1` as it's starting from 0 index and ZonedDateTime index starts from 1.
                        TimeUtils.toZonedDateTime(dd, mm + 1, yyyy, hour, minute).let {
                            view.setText(TimeUtils.dateTimeString(it))
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
                        .setTitle(context.getString(R.string.remove_text, view.hint.toString()))
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