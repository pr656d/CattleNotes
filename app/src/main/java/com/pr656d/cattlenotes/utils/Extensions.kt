package com.pr656d.cattlenotes.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.local.db.Converters
import com.pr656d.cattlenotes.data.model.Animal
import com.pr656d.cattlenotes.data.model.Cattle
import java.text.SimpleDateFormat
import java.util.*

// region TextInputEditText

fun TextInputEditText.focus() {
    // Set focusable to true.
    isFocusableInTouchMode = true
    // Take view in focus.
    requestFocus()
}

// end region

// region Date
fun View.pickADate(
    onDateSet: (view: DatePicker, dd: Int, mm: Int, yyyy: Int) -> Unit
) {
    // Create date picker dialog.
    val dialog = DatePickerDialog(
        context, R.style.DatePicker,
        DatePickerDialog.OnDateSetListener { v, yyyy, mm, dd ->
            onDateSet(v, dd, mm, yyyy)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    // Cancel button click handler.
    dialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        context.getString(R.string.cancel)
    ) { _, which ->
        // Reset focus to false.
        if (which == DialogInterface.BUTTON_NEGATIVE)
            isFocusableInTouchMode = false
    }

    // Disable future date selection
    dialog.datePicker.maxDate = System.currentTimeMillis()

    dialog.show()   // Show the dialog.
}
// end region

// region dialog

/**
 * Wrapper function for showing Material dialog.
 */
fun Context.showDialog(
    @StringRes title: Int,
    @StringRes message: Int? = null,
    @StringRes positiveTextId: Int,
    onPositiveSelected: () -> Unit = {},
    @StringRes negativeTextId: Int? = null,
    onNegativeSelected: () -> Unit = {},
    @StringRes neutralTextId: Int? = null,
    onNeutralSelected: () -> Unit = {}
) {
    val dialog = MaterialAlertDialogBuilder(this)
    dialog.setTitle(title)
    message?.let { dialog.setMessage(it) }
    dialog.setPositiveButton(positiveTextId) { _, which ->
        if (which == AlertDialog.BUTTON_POSITIVE)
            onPositiveSelected()
    }
    negativeTextId?.let {
        dialog.setNegativeButton(it) { _, which ->
            if (which == AlertDialog.BUTTON_NEGATIVE)
                onNegativeSelected()
        }
    }
    neutralTextId?.let {
        dialog.setNeutralButton(it) { _, which ->
            if (which == AlertDialog.BUTTON_NEUTRAL)
                onNeutralSelected()
        }
    }
    dialog.create().show()
}
// end region


// region Soft input

fun FragmentActivity.hideKeyboard(view: View) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}

// region end

inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

// region date to string
fun Context.toDate(dayOfMonth: Int, month: Int, year: Int): Date? {
    val string = "$dayOfMonth/${month + 1}/$year"
    return SimpleDateFormat(
        "dd/MM/yyyy",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            resources.configuration.locales.get(0)
        else
            resources.configuration.locale
    ).parse(string)
}

fun Context.getString(date: Date): String = DateFormat.getMediumDateFormat(this).format(date)

@SuppressLint("SimpleDateFormat")
fun Date.toFormattedString(): String = SimpleDateFormat("dd/MM/yyyy").format(this)

// end region

// region converters

fun String.toType(): Animal.Type = Converters().fromStringToType(this)

fun String.toBreed(): Cattle.Breed = Converters().fromStringToBreed(this)

fun String.toGroup(): Cattle.Group = Converters().fromStringToGroup(this)

// end region

// region String util

/**
 * TextWatcher for input gives empty string ("") which causes parse error for conversion to different object.
 */
fun String?.toDateOrNull(): String? = if (isNullOrBlank()) null else this

// end region