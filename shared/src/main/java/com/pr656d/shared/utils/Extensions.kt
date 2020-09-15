/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.utils

import android.telephony.SmsMessage
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.model.Milk
import com.pr656d.shared.data.db.Converters
import com.pr656d.shared.sms.NotAMilkSmsException
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

// region converters

fun String.toType(): AnimalType = Converters().fromStringToAnimalType(this)

fun String.toGroup(): Cattle.Group? = Converters().fromStringToGroup(this)

fun Char.toMilkOf(): Milk.MilkOf = Milk.MilkOf.INSTANCES.filterKeys {
    // Compare with first letter of key.
    it.first() == this
}.values.first() // Return the first match.

fun String.toMilkOf(): Milk.MilkOf = Converters().fromStringToMilkOf(this)

fun String.toMilkSource(): Milk.Source = Converters().fromStringToMilkSource(this)

fun String.toMilkSmsSource(): Milk.Source.Sms =
    Converters().fromStringToMilkSource(this) as? Milk.Source.Sms
        ?: throw IllegalArgumentException(
            "Invalid String : Can not convert $this to Milk.Source.Sms"
        )

// region Time
fun Long.toLocalDate(): LocalDate = TimeUtils.toLocalDate(this)

fun Long.toLocalTime(): LocalTime = TimeUtils.toLocalTime(this)

fun LocalDate.toLong(): Long = TimeUtils.toLong(this)

fun LocalTime.toLong(): Long = TimeUtils.toLong(this)

fun ZonedDateTime.toEpochMilli(): Long = TimeUtils.toEpochMilli(this)

fun Long.toZonedDateTime(): ZonedDateTime = TimeUtils.toZonedDateTime(this)
// end region

// region Cattle
fun Cattle.nameOrTagNumber(): String = if (!this.name.isNullOrBlank())
    name!!
else
    tagNumber.toString()
// end region

// region SMS

/**
 * Returns [Milk.Source.Sms] on bases of [SmsMessage.getOriginatingAddress].
 */
@Throws(NotAMilkSmsException::class)
fun SmsMessage.getSmsSourceOrThrow() = originatingAddress.getSmsSource()

@Throws(NotAMilkSmsException::class)
fun String?.getSmsSource() = Milk.Source.Sms.INSTANCES.values.find {
    it.ORIGINATING_ADDRESS == this
} ?: throw NotAMilkSmsException()

@Throws(Exception::class)
fun SmsMessage.getDisplayMessageBodyOrThrow(): String =
    displayMessageBody ?: throw Exception("Message body not found")
// end region
