package com.pr656d.model

import androidx.room.*
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

@Entity(
    tableName = "breedingList",
    foreignKeys = [
        ForeignKey(
            entity = Cattle::class,
            parentColumns = ["id"],
            childColumns = ["cattleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Breeding(
    @SerializedName("cattleId")
    @ColumnInfo(name = "cattleId", index = true)
    val cattleId: String,

    /**
     * Artificial Insemination date as String.
     */
    @SerializedName("ai")
    @Embedded(prefix = "ai")
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
    @PrimaryKey
    var id: String = ""

    data class ArtificialInseminationInfo(
        @SerializedName("Date")
        @ColumnInfo(name = "Date")
        val date: LocalDate,

        @SerializedName("DidBy")
        @ColumnInfo(name = "DidBy")
        val didBy: String?,

        @SerializedName("BullName")
        @ColumnInfo(name = "BullName")
        val bullName: String?,

        @SerializedName("StrawCode")
        @ColumnInfo(name = "StrawCode")
        val strawCode: String?
    )

    data class BreedingEvent(
        @SerializedName("ExpectedOn")
        @ColumnInfo(name = "ExpectedOn")
        val expectedOn: LocalDate,

        @SerializedName("Status")
        @ColumnInfo(name = "Status")
        val status: Boolean? = null,

        @SerializedName("DoneOn")
        @ColumnInfo(name = "DoneOn")
        val doneOn: LocalDate? = null
    ) {
        @Ignore
        val statusString = when (status) {
            null -> "None"
            true -> "Positive"
            false -> "Negative"
        }
    }
}