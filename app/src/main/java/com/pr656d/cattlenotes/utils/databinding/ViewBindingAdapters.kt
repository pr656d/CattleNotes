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
package com.pr656d.cattlenotes.utils.databinding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.utils.CircularOutlineProvider
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import timber.log.Timber

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

/** Set text on a [TextView] from a [LocalDate] resource. */
@BindingAdapter("android:text")
fun setText(view: TextView, date: LocalDate?) {
    date?.let {
        view.text = TimeUtils.dateString(it)
    }
}

/** Set text on [TextView] from a [LocalTime] resource */
@BindingAdapter("android:text")
fun setText(view: TextView, time: LocalTime?) {
    time?.let {
        view.text = TimeUtils.timeString(it)
    }
}

/** Set text on [TextView] from a [ZonedDateTime] resource */
@BindingAdapter("android:text")
fun setText(view: TextView, zonedDateTime: ZonedDateTime?) {
    zonedDateTime?.let {
        view.text = TimeUtils.dateTimeString(zonedDateTime)
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

@BindingAdapter(value = ["imageUri", "placeholder"], requireAll = false)
fun imageUri(imageView: ImageView, imageUri: Uri?, placeholder: Drawable?) {
    when (imageUri) {
        null -> {
            Timber.d("Unsetting image url")
            Glide.with(imageView)
                .load(placeholder)
                .into(imageView)
        }
        else -> {
            Glide.with(imageView)
                .load(imageUri)
                .apply(RequestOptions().placeholder(placeholder))
                .into(imageView)
        }
    }
}

@BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
fun imageUrl(imageView: ImageView, imageUrl: String?, placeholder: Drawable?) {
    imageUri(imageView, imageUrl?.toUri(), placeholder)
}

@BindingAdapter("clipToCircle")
fun clipToCircle(view: View, clip: Boolean) {
    view.clipToOutline = clip
    view.outlineProvider = if (clip) CircularOutlineProvider else null
}
