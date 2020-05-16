package com.pr656d.cattlenotes.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.model.Theme
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import timber.log.Timber

// region context
fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}
// end region

// region TextInputEditText

fun TextInputEditText.focus() {
    // Set focusable to true.
    isFocusableInTouchMode = true
    // Take view in focus.
    requestFocus()
}

// end region

// region Date picker
fun View.pickADate(
    onDateCancelled: () -> Unit = {},
    onDateSet: (view: DatePicker, dd: Int, mm: Int, yyyy: Int) -> Unit
) {
    /**
     * Layout brakes if it's called in parent context of dialog.
     * Always use Activity context to not break UI of picker.
     */
    val context = context.activity() ?: let {
        Timber.d("Activity not found")
        return
    }

    val currentDate = LocalDate.now()

    // Create date picker dialog.
    val dialog = DatePickerDialog(
        context, R.style.DatePicker,
        DatePickerDialog.OnDateSetListener { v, yyyy, mm, dd ->
            onDateSet(v, dd, mm, yyyy)
        },
        currentDate.year,
        currentDate.monthValue,
        currentDate.dayOfMonth
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

// region time picker
fun View.pickATime(
    onTimeCancelled: () -> Unit = {},
    onTimeSet: (view: TimePicker, hour: Int, minute: Int) -> Unit
) {
    /**
     * Layout brakes if it's called in parent context of dialog.
     * Always use Activity context to not break UI of picker.
     */
    val context = context.activity() ?: let {
        Timber.d("Activity not found")
        return
    }

    val currentTime = LocalTime.now()

    // Create time picker dialog.
    val dialog = TimePickerDialog(
        context, R.style.TimePicker,
        TimePickerDialog.OnTimeSetListener { v, hourOfDay, minute ->
            onTimeSet(v, hourOfDay, minute)
        },
        currentTime.hour,
        currentTime.minute,
        false
    )

    // Cancel button click handler.
    dialog.setButton(
        DialogInterface.BUTTON_NEGATIVE,
        context.getString(R.string.cancel)
    ) { _, which ->
        // Reset focus to false.
        if (which == DialogInterface.BUTTON_NEGATIVE)
            onTimeCancelled()
    }

    dialog.show()   // Show the dialog.
}
// end region

// region date time
fun View.pickADateTime(
    onDateTimeCancelled: () -> Unit = {},
    onDateTimeSet: (
        view: View,
        dd: Int, mm: Int, yyyy: Int,
        hour: Int, minute: Int
    ) -> Unit
) {
    pickADate(
        onDateSet = { _, dd, mm, yyyy ->
            pickATime(
                onTimeSet = { _, hour, minute ->
                    onDateTimeSet(this, dd, mm, yyyy, hour, minute)
                },
                onTimeCancelled = {
                    onDateTimeCancelled()
                }
            )
        },
        onDateCancelled = {
            onDateTimeCancelled()
        }
    )
}
// region end

// region Soft input

fun hideKeyboard(view: View) {
    (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.rootView.windowToken, 0)
    view.clearFocus()
}

// region end

inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

// region viewmodel

/**
 * Like [Fragment.viewModelProvider] for Fragments that want a [ViewModel] scoped to the parent
 * Fragment.
 */
inline fun <reified VM : ViewModel> Fragment.parentViewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProvider(parentFragment!!, provider).get(VM::class.java)

// region end

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

// region Permissions

fun Context.isPermissionGranted(permission: String) =
    (ContextCompat.checkSelfPermission(this, permission)
        == PackageManager.PERMISSION_GRANTED)

// end region

/**
 * Calculated the widest line in a [StaticLayout].
 */
fun StaticLayout.textWidth(): Int {
    var width = 0f
    for (i in 0 until lineCount) {
        width = width.coerceAtLeast(getLineWidth(i))
    }
    return width.toInt()
}

fun newStaticLayout(
    source: CharSequence,
    paint: TextPaint,
    width: Int,
    alignment: Layout.Alignment,
    spacingmult: Float,
    spacingadd: Float,
    includepad: Boolean
): StaticLayout {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder.obtain(source, 0, source.length, paint, width).apply {
            setAlignment(alignment)
            setLineSpacing(spacingadd, spacingmult)
            setIncludePad(includepad)
        }.build()
    } else {
        @Suppress("DEPRECATION")
        (StaticLayout(source, paint, width, alignment, spacingmult, spacingadd, includepad))
    }
}