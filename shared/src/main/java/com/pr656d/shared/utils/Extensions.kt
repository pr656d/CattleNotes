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
}.values.first()    // Return the first match.

fun String.toMilkOf(): Milk.MilkOf = Converters().fromStringToMilkOf(this)

fun String.toMilkSource(): Milk.Source = Converters().fromStringToMilkSource(this)

fun String.toMilkSmsSource(): Milk.Source.Sms =
    Converters().fromStringToMilkSource(this) as? Milk.Source.Sms
        ?: throw IllegalArgumentException("Invalid String : Can not convert $this to Milk.Source.Sms")

// region Time
fun Long.toLocalDate(): LocalDate = TimeUtils.toLocalDate(this)

fun Long.toLocalTime(): LocalTime = TimeUtils.toLocalTime(this)

fun LocalDate.toLong(): Long = TimeUtils.toLong(this)

fun LocalTime.toLong(): Long = TimeUtils.toLong(this)

fun ZonedDateTime.toEpochMilli(): Long = TimeUtils.toEpochMilli(this)

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