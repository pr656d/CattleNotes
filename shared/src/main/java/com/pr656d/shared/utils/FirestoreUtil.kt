/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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