package com.pr656d.cattlenotes.model

/**
 * Describes a animal.
 */
data class Animal(
    val tagNumber: String,

    var name: String? = null,

    var type: AnimalType,

    var breed: AnimalBreed? = null,

    var group: AnimalGroup? = null,

    var calving: Int = 0,

    var aiDate: String? = null,

    var repeatHeatDate: String? = null,

    var pregnancyCheckDate: String? = null,

    var dryOffDate: String? = null
) {
    /**
     * Represents type of animal e.g. cow, bull etc.
     */
    enum class AnimalType(val displayName: String) {
        COW("Cow"),
        BUFFALO("Buffalo"),
        BULL("Bull")
    }

    /**
     * Represents breed of animal e.g. HF, Jerry, Gir etc.
     */
    enum class AnimalBreed(val displayName: String) {
        HF("HF"),
        JERRY("Jerry"),
        GIR("Gir"),
        KANKREJ("Kankrej"),
        SHAHIVAL("Shahival")
    }

    /**
     * Represents group of animal e.g. heifer, milking etc.
     */
    enum class AnimalGroup(val displayName: String) {
        HEIFER("Heifer"),
        MILKING("Milking"),
        DRY("Dry"),
        CALF_MALE("Calf male"),
        CALF_FEMALE("Calf female")
    }
}