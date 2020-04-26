package com.pr656d.model

sealed class AnimalType(val displayName: String) {
    object Cow : AnimalType("Cow") {
        override fun toString(): String = displayName
    }

    object Buffalo : AnimalType("Buffalo") {
        override fun toString(): String = displayName
    }

    object Bull : AnimalType("Bull") {
        override fun toString(): String = displayName
    }
}