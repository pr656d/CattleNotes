package com.pr656d.shared.utils

import android.telephony.SmsMessage
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.model.Milk
import com.pr656d.shared.data.db.Converters
import com.pr656d.shared.sms.NotAMilkSmsException
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

// region converters

fun String.toType(): AnimalType = Converters().fromStringToAnimalType(this)

fun String.toGroup(): Cattle.Group? = Converters().fromStringToGroup(this)

/**
 * Return the [Milk.Shift] for [Char]. Assuming [Char] matches to only one key's first letter of
 * [Milk.Shift.INSTANCES].
 */
fun Char.toMilkShift(): Milk.Shift = Milk.Shift.INSTANCES.filterKeys {
    // Compare with first letter of key.
    it.first() == this
}.values.first()    // Return the first match.

fun String.toMilkShift(): Milk.Shift = Converters().fromStringToShift(this)

fun Char.toMilkOf(): Milk.MilkOf = Milk.MilkOf.INSTANCES.filterKeys {
    // Compare with first letter of key.
    it.first() == this
}.values.first()    // Return the first match.

fun String.toMilkOf(): Milk.MilkOf = Converters().fromStringToMilkOf(this)

fun String.toMilkSource(): Milk.Source = Converters().fromStringToMilkSource(this)

// end region

// region ZonedDateTime
fun ZonedDateTime.toEpochMilli(): Long = TimeUtils.toEpochMilli(this)
// end region

// region Time
fun Long.toLocalDate(): LocalDate = TimeUtils.toLocalDate(this)

fun LocalDate.toLong(): Long = TimeUtils.toLong(this)

fun Long.toZonedDateTime(): ZonedDateTime = TimeUtils.toZonedDateTime(this)
// end region

// region Cattle
fun Cattle.nameOrTagNumber(): String = if (!this.name.isNullOrBlank()) name!! else tagNumber.toString()
// end region

// region SMS
@Throws(NotAMilkSmsException::class)
fun SmsMessage.getSmsSenderTypeOrThrow(): Milk.Source.Sms {
    return when (this.originatingAddress) {
        Milk.Source.Sms.BGAMAMCS.SENDER_ADDRESS -> Milk.Source.Sms.BGAMAMCS
        else -> throw NotAMilkSmsException(this)
    }
}

@Throws(Exception::class)
fun SmsMessage.getDisplayMessageBodyOrThrow(): String {
    return this.displayMessageBody ?: throw Exception("Message body not found")
}
// end region