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

/** Set text on a [TextView] from a [LocalDate] resource. */
@BindingAdapter("android:text")
fun setText(view: TextView, date: LocalDate?) {
    date?.let {
        view.text = TimeUtils.dateString(it)
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