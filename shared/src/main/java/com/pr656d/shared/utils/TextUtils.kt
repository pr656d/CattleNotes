package com.pr656d.shared.utils

/**
 * Could not use TextUtils methods directly because in Unit testing
 * Mockito throws "TextUtils not mocked error" TextUtils is part of androidx framework.
 * Use TextUtils implementation directly instead as a project's TextUtils.
 */

/**
 * Returns whether the given CharSequence contains only digits.
 */
fun CharSequence.isDigitsOnly(): Boolean {
    val len = this.length
    var cp: Int
    var i = 0
    while (i < len) {
        cp = Character.codePointAt(this, i)
        if (!Character.isDigit(cp)) {
            return false
        }
        i += Character.charCount(cp)
    }
    return true
}
