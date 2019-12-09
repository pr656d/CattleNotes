package com.pr656d.cattlenotes.utils.common

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat


// region date to string
/**
 * Convert to local format and return string.
 */
fun Context.parseToString(dayOfMonth: Int, month: Int, year: Int): String =
    SimpleDateFormat(
        "dd/MM/yyyy",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            resources.configuration.locales.get(0)
        else
            resources.configuration.locale
    ).run {
        // In Android SDK API index of month start from 0. +1 to get correct month.
        format(parse("$dayOfMonth/${month+1}/$year")!!)
    }

// end region