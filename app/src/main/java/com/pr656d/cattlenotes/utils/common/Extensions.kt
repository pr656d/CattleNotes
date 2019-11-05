package com.pr656d.cattlenotes.utils.common

import android.content.Context
import android.content.Intent
import android.os.Bundle

fun <T> Context.startActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    startActivity(
        Intent(this, it)
            .apply { putExtras(Bundle().apply { extras }) }
    )
}