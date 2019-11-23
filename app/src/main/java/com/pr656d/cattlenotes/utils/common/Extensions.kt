package com.pr656d.cattlenotes.utils.common

import android.app.Activity
import android.os.Build
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat


// region date to string
/**
 * Convert to local format and return string.
 */
fun Activity.parseToString(dayOfMonth: Int, month: Int, year: Int): String? =
    SimpleDateFormat(
        "dd/MM/yyyy",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            resources.configuration.locales.get(0)
        else
            resources.configuration.locale
    ).run {
        parse("$dayOfMonth/$month/$year")?.let {
            format(it)
        }
    }

fun Fragment.parseToString(dayOfMonth: Int, month: Int, year: Int): String =
    SimpleDateFormat(
        "dd/MM/yyyy",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            resources.configuration.locales.get(0)
        else
            resources.configuration.locale
    ).run {
        format(parse("$dayOfMonth/$month/$year")!!)
    }

// end region