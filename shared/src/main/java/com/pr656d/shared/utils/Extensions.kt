package com.pr656d.shared.utils

import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.shared.data.db.Converters
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

// region converters

fun String.toType(): AnimalType = Converters().fromStringToAnimalType(this)

fun String.toBreed(): Cattle.Breed = Converters().fromStringToBreed(this)

fun String.toGroup(): Cattle.Group = Converters().fromStringToGroup(this)

// end region

// region ZonedDateTime
fun ZonedDateTime.toEpochMilli(): Long = TimeUtils.toEpochMilli(this)
// endregion

// region LocalDate
fun Long.toLocalDate(): LocalDate = TimeUtils.toLocalDate(this)

fun LocalDate.toEpochMilli(): Long = TimeUtils.toEpochMilli(this)

fun LocalDate.toLong(): Long = TimeUtils.toLong(this)
// endregion

// region Cattle
fun Cattle.nameOrTagNumber(): String = if (!this.name.isNullOrBlank()) name!! else tagNumber.toString()
// endregion