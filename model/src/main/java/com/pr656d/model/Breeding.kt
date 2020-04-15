package com.pr656d.model

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.pr656d.model.Breeding.BreedingEvent
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
     * Artificial Insemination date.
     */
    @SerializedName("artificialInsemination")
    @Embedded(prefix = "artificialInsemination")
    val artificialInsemination: ArtificialInseminationInfo,

    /**
     * Holds repeat heat data as [BreedingEvent].
     */
    @SerializedName("repeatHeat")
    @Embedded(prefix = "repeatHeat")
    val repeatHeat: BreedingEvent,

    /**
     * Holds pregnancy check data as [BreedingEvent].
     */
    @SerializedName("pregnancyCheck")
    @Embedded(prefix = "pregnancyCheck")
    val pregnancyCheck: BreedingEvent,

    /**
     * Holds dry off data as [BreedingEvent].
     */
    @SerializedName("dryOff")
    @Embedded(prefix = "dryOff")
    val dryOff: BreedingEvent,

    /**
     * Holds calving data as [BreedingEvent].
     */
    @SerializedName("calving")
    @Embedded(prefix = "calving")
    val calving: BreedingEvent
) {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String = ""

    /**
     * When
     *
     * 1. repeat heat status is 'Positive' or
     * 2. pregnancy check status is 'Negative'
     * 3. calving status is 'Positive'
     *
     * then breeding cycle is completed.
     */
    @Ignore
    @SerializedName("breedingCompleted")
    val breedingCompleted: Boolean =
        repeatHeat.status == true || pregnancyCheck.status == false || calving.status == true

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
        @Ignore
        val type: Type?,

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

        /**
         * Eliminate Room error : Entities and POJOs must have a usable public constructor.
         * https://github.com/android/architecture-components-samples/issues/421#issuecomment-533217060
         */
        constructor(
            expectedOn: LocalDate,
            status: Boolean? = null,
            doneOn: LocalDate? = null
        ) : this(null, expectedOn, status, doneOn)

        @Ignore
        val statusString: String = when (status) {
            null -> "None"
            true -> "Positive"
            false -> "Negative"
        }

        enum class Type(val displayName: String) {
            REPEAT_HEAT("Repeat heat"),
            PREGNANCY_CHECK("Pregnancy check"),
            DRY_OFF("Dry off"),
            CALVING("Calving")
        }
    }
}