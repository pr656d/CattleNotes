package com.pr656d.model

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

@Entity(
    tableName = "breeding_cycle",
    foreignKeys = [
        ForeignKey(
            entity = Cattle::class,
            parentColumns = ["id"],
            childColumns = ["cattle_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class BreedingCycle(
    @SerializedName("cattle_id")
    @ColumnInfo(name = "cattle_id", index = true)
    val cattleId: Long,

    @SerializedName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false,

    /**
     * Artificial Insemination date as String.
     */
    @SerializedName("ai_")
    @Embedded(prefix = "ai_")
    val artificialInsemination: ArtificialInseminationInfo? = null,

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
    val pregnancyCheck: BreedingEvent? = null,

    /**
     * Depends on pregnancy check status.
     */
    @SerializedName("dry_off")
    @Embedded(prefix = "dry_off_")
    val dryOff: BreedingEvent? = null,

    @SerializedName("calving")
    @Embedded(prefix = "calving_")
    val calving: BreedingEvent? = null
) {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    data class ArtificialInseminationInfo(
        @SerializedName("date")
        @ColumnInfo(name = "date")
        val date: LocalDate,

        @SerializedName("did_by")
        @ColumnInfo(name = "did_by")
        val didBy: String?,

        @SerializedName("bull_name")
        @ColumnInfo(name = "bull_name")
        val bullName: String?,

        @SerializedName("straw_code")
        @ColumnInfo(name = "straw_code")
        val strawCode: String?
    )

    data class BreedingEvent(
        @SerializedName("expected_on")
        @ColumnInfo(name = "expected_on")
        val expectedOn: LocalDate,

        @SerializedName("status")
        @ColumnInfo(name = "status")
        val status: Boolean? = null,

        @SerializedName("done_on")
        @ColumnInfo(name = "done_on")
        val doneOn: LocalDate? = null
    )
}