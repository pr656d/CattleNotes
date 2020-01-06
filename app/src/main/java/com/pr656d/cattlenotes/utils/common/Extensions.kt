package com.pr656d.cattlenotes.utils.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.pr656d.cattlenotes.data.local.db.Converters
import com.pr656d.cattlenotes.data.model.Animal
import com.pr656d.cattlenotes.data.model.Cattle
import java.text.SimpleDateFormat
import java.util.*


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

@SuppressLint("SimpleDateFormat")
fun String.toDate(): Date = Date(SimpleDateFormat("dd/MM/yyyy").parse(this)!!.time)

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