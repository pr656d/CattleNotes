package com.pr656d.model

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.Breeding.BreedingEvent.Type.*
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
     * Holds repeat heat data as [BreedingEvent] with UNKNOWN type.
     * @see repeatHeat  To access repeatHeat data.
     */
    @SerializedName("repeatHeat")
    @Embedded(prefix = "repeatHeat")
    val repeat_heat: BreedingEvent,

    /**
     * Holds pregnancy check data as [BreedingEvent] with UNKNOWN type.
     * @see pregnancyCheck  To access pregnancy check data.
     */
    @SerializedName("pregnancyCheck")
    @Embedded(prefix = "pregnancyCheck")
    val pregnancy_check: BreedingEvent,

    /**
     * Holds dry off data as [BreedingEvent] with UNKNOWN type.
     * @see dryOff  To access dry off data.
     */
    @SerializedName("dryOff")
    @Embedded(prefix = "dryOff")
    val dry_off: BreedingEvent,

    /**
     * Holds calving data as [BreedingEvent] with UNKNOWN type.
     * @see calving  To access calving data.
     */
    @SerializedName("calving")
    @Embedded(prefix = "calving")
    val calving_: BreedingEvent
) {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String = ""
        set(value) = if (!value.isBlank()) {
            field = value
        } else {
            throw IllegalArgumentException("Breeding id is blank")
        }

    /**
     * Our primary goal is when [BreedingEvent] is used it holds it's type.
     *
     * [Breeding] fields it self represents type of [BreedingEvent],
     * So no need to explicitly add type while creating [Breeding].
     * Ex:  val breeding = Breeding(
     *          ...,
     *          repeat_heat = BreedingEvent(...),
     *          ...
     *      )
     *
     * Allow calls like :
     *      breeding.repeatHeat.type (returns REPEAT_HEAT)
     */
    @Ignore
    @SerializedName("repeatHeatWithType")
    val repeatHeat: BreedingEvent = repeat_heat.apply { type = REPEAT_HEAT }

    @Ignore
    @SerializedName("pregnancyCheckWithType")
    val pregnancyCheck: BreedingEvent = pregnancy_check.apply { type = PREGNANCY_CHECK }

    @Ignore
    @SerializedName("dryOffWithType")
    val dryOff: BreedingEvent = dry_off.apply { type = DRY_OFF }

    @Ignore
    @SerializedName("calvingWithType")
    val calving: BreedingEvent = calving_.apply { type = CALVING }

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

    /**
     * Provides next breeding event.
     */
    @Ignore
    @SerializedName("nextBreedingEvent")
    val nextBreedingEvent: BreedingEvent? =
        when {
            breedingCompleted -> null
            repeatHeat.status == null -> repeatHeat
            pregnancyCheck.status == null -> pregnancyCheck
            dryOff.status == null -> dryOff
            calving.status == null -> calving
            else -> throw IllegalStateException("Can not generate next breeding event: Breeding data is $this")
        }

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

        /**
         * Type of breeding event. Initially it's unknown.
         */
        @Ignore
        @SerializedName("type")
        var type: Type = UNKNOWN

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
            CALVING("Calving"),
            UNKNOWN("Unknown")
        }
    }
}