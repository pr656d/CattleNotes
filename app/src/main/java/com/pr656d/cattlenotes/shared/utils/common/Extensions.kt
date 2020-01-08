package com.pr656d.cattlenotes.shared.utils.common

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime


/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

// region ZonedDateTime
@RequiresApi(Build.VERSION_CODES.O)
fun ZonedDateTime.toEpochMilli() = this.toInstant().toEpochMilli()
// endregion