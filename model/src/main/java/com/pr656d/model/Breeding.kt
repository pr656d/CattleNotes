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
    @SerializedName("artificialInsemination")
    @Embedded(prefix = "artificialInsemination")
    val artificialInsemination: ArtificialInseminationInfo? = null,

    /**
     * Depends on AI date.
     */
    @SerializedName("repeatHeat")
    @Embedded(prefix = "repeatHeat")
    val repeatHeat: BreedingEvent? = null,

    /**
     * Depends on repeat heat status.
     */
    @SerializedName("pregnancyCheck")
    @Embedded(prefix = "pregnancyCheck")
    val pregnancyCheck: BreedingEvent? = null,

    /**
     * Depends on pregnancy check status.
     */
    @SerializedName("dryOff")
    @Embedded(prefix = "dryOff")
    val dryOff: BreedingEvent? = null,

    @SerializedName("calving")
    @Embedded(prefix = "calving")
    val calving: BreedingEvent? = null
) {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String = ""

    data class ArtificialInseminationInfo(
        @SerializedName("date")
        @ColumnInfo(name = "Date")
        val date: LocalDate,

        @SerializedName("didBy")
        @ColumnInfo(name = "DidBy")
        val didBy: String?,

        @SerializedName("bullName")
        @ColumnInfo(name = "BullName")
        val bullName: String?,

        @SerializedName("strawCode")
        @ColumnInfo(name = "StrawCode")
        val strawCode: String?
    )

    data class BreedingEvent(
        @SerializedName("expectedOn")
        @ColumnInfo(name = "ExpectedOn")
        val expectedOn: LocalDate,

        @SerializedName("status")
        @ColumnInfo(name = "Status")
        val status: Boolean? = null,

        @SerializedName("doneOn")
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