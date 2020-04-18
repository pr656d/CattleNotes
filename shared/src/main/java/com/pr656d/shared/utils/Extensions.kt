package com.pr656d.shared.utils

import com.pr656d.model.Animal
import com.pr656d.model.Cattle
import com.pr656d.shared.data.db.Converters

// region converters

fun String.toType(): Animal.Type = Converters().fromStringToAnimalType(this)

fun String.toBreed(): Cattle.Breed = Converters().fromStringToBreed(this)

fun String.toGroup(): Cattle.Group = Converters().fromStringToGroup(this)

// end region