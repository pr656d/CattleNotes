package com.pr656d.cattlenotes.data.model

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "breeding_cycle",
    foreignKeys = [
        ForeignKey(
            entity = Cattle::class,
            parentColumns = ["tag_number"],
            childColumns = ["tag_number"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class BreedingCycle(
    @SerializedName("tag_number")
    @ColumnInfo(name = "tag_number", index = true)
    val tagNumber: Long,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    val type: Animal.Type,

    @SerializedName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,

    /**
     * Artificial Insemination date as String.
     */
    @SerializedName("artificial_insemination")
    @ColumnInfo(name = "artificial_insemination")
    val artificialInsemination: Date?,

    /**
     * Depends on AI date.
     */
    @SerializedName("repeat_heat")
    @Embedded(prefix = "repeat_heat_")
    val repeatHeat: BreedingEvent? = null,

    /**
     * Depends on repeat heat status.
     */
    @SerializedName("pregnancy_check")
    @Embedded(prefix = "pregnancy_check_")
    val pregnancyCheckDate: BreedingEvent? = null,

    /**
     * Depends on pregnancy check status.
     */
    @SerializedName("dry_off")
    @Embedded(prefix = "dry_off_")
    val dryOffDate: BreedingEvent? = null,

    @SerializedName("calving")
    @Embedded(prefix = "calving_")
    val calvingDate: BreedingEvent? = null
) {

    @SerializedName("breeding_cycle_id")
    @ColumnInfo(name = "breeding_cycle_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    data class BreedingEvent(
        @SerializedName("expected_on")
        @ColumnInfo(name = "expected_on")
        val expectedOn: Date,

        @SerializedName("status")
        @ColumnInfo(name = "status")
        val status: Boolean = false,

        @SerializedName("done_on")
        @ColumnInfo(name = "done_on")
        val doneOn: Date? = null
    )
}