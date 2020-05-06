package com.pr656d.model

sealed class AnimalType(val displayName: String) {
    companion object {
        /**
         * All instances of [AnimalType].
         */
        val INSTANCES: Map<String, AnimalType> by lazy {
            mapOf(
                Cow.displayName to Cow,
                Buffalo.displayName to Buffalo,
                Bull.displayName to Bull
            )
        }
    }

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