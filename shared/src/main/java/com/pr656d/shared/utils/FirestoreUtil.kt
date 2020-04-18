package com.pr656d.shared.utils

import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

object FirestoreUtil {
    private const val AUTO_ID_LENGTH = 20
    private const val AUTO_ID_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    private val rand: Random = SecureRandom().asKotlinRandom()

    /** Return id. */
    fun autoId(): String = StringBuilder().run {
        val maxRandom = AUTO_ID_ALPHABET.length
        repeat(AUTO_ID_LENGTH) {
            append(AUTO_ID_ALPHABET[rand.nextInt(maxRandom)])
        }
        toString()
    }
}