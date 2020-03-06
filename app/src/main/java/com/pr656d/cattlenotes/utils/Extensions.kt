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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.local.db.Converters
import com.pr656d.cattlenotes.data.model.Animal
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.model.Theme
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

// region MaterialAlertDialogBuilder

fun MaterialAlertDialogBuilder.onPositiveSelected(
    which: Int, block: MaterialAlertDialogBuilder.() -> Unit
) {
    if (which == AlertDialog.BUTTON_POSITIVE)
        block()
}

fun MaterialAlertDialogBuilder.onNegativeSelected(
    which: Int, block: MaterialAlertDialogBuilder.() -> Unit
) {
    if (which == AlertDialog.BUTTON_NEGATIVE)
        block()
}

fun MaterialAlertDialogBuilder.onNeutralSelected(
    which: Int, block: MaterialAlertDialogBuilder.() -> Unit
) {
    if (which == AlertDialog.BUTTON_NEUTRAL)
        block()
}

// end region

// region Date
fun View.pickADate(
    onDateCancelled: () -> Unit = {},
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
            onDateCancelled()
    }

    // Disable future date selection
    dialog.datePicker.maxDate = System.currentTimeMillis()

    dialog.show()   // Show the dialog.
}
// end region

// region Soft input

fun hideKeyboard(view: View) {
    (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
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

fun Context.toDate(dateString: String): Date? {
    var date = Date()
    val pattern = SimpleDateFormat(
        "dd/MM/yyyy",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            resources.configuration.locales.get(0)
        else
            resources.configuration.locale
    ).run {
        parse(dateString)
        format(this)
    }
    return date
}

fun Context.getString(date: Date): String = DateFormat.getMediumDateFormat(this).format(date)

// end region

// region converters

fun String.toType(): Animal.Type = Converters().fromStringToType(this)

fun String.toBreed(): Cattle.Breed = Converters().fromStringToBreed(this)

fun String.toGroup(): Cattle.Group = Converters().fromStringToGroup(this)

// end region

// region Theme
/**
 * Having to suppress lint. Bug raised: 128789886
 */
@SuppressLint("WrongConstant")
fun AppCompatActivity.updateForTheme(theme: Theme) = when (theme) {
    Theme.DARK -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
    Theme.LIGHT -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
    Theme.SYSTEM -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    Theme.BATTERY_SAVER -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
}
// end region