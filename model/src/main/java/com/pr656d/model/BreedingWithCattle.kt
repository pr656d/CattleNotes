package com.pr656d.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * It contains [Breeding] and corresponding [Cattle] detail.
 */
data class BreedingWithCattle(
    @Relation(
        parentColumn = "cattle_id",
        entityColumn = "id"
    )
    val cattle: Cattle,

    @Embedded
    val breeding: Breeding
)