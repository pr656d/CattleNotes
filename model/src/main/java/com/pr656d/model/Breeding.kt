/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String,

    @SerializedName("cattleId")
    @ColumnInfo(name = "cattleId", index = true)
    val cattleId: String,

    /**
     * Artificial Insemination date.
     */
    @SerializedName("artificialInsemination")
    @Embedded(prefix = "artificialInsemination")
    val artificialInsemination: ArtificialInsemination,

    /**
     * Repeat heat.
     */
    @SerializedName("repeatHeat")
    @Embedded(prefix = "repeatHeat")
    val repeatHeat: BreedingEvent.RepeatHeat,

    /**
     * Pregnancy check.
     */
    @SerializedName("pregnancyCheck")
    @Embedded(prefix = "pregnancyCheck")
    val pregnancyCheck: BreedingEvent.PregnancyCheck,

    /**
     * Dry off.
     */
    @SerializedName("dryOff")
    @Embedded(prefix = "dryOff")
    val dryOff: BreedingEvent.DryOff,

    /**
     * Calving.
     */
    @SerializedName("calving")
    @Embedded(prefix = "calving")
    val calving: BreedingEvent.Calving
) {
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
     * Order of checks matters
     *  1. Check Breeding completed or not
     *  2. Check repeat heat status
     *  3. Check pregnancy check status
     *  4. Check dry off status
     *  5. Check calving status
     */
    @Ignore
    @SerializedName("nextBreedingEvent")
    val nextBreedingEvent: BreedingEvent? =
        when {
            breedingCompleted -> null   // Breeding completed
            repeatHeat.status == null -> repeatHeat // Repeat heat is pending
            pregnancyCheck.status == null -> pregnancyCheck // Pregnancy check is pending
            dryOff.status == null -> dryOff // Dry off is pending
            calving.status == null -> calving   // Calving is pending
            else -> throw IllegalStateException("Can not generate next breeding event: Breeding data is $this")
        }

    data class ArtificialInsemination(
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

    sealed class BreedingEvent(
        @SerializedName("expectedOn")
        @ColumnInfo(name = "ExpectedOn")
        open val expectedOn: LocalDate,

        @SerializedName("status")
        @ColumnInfo(name = "Status")
        open val status: Boolean? = null,

        @SerializedName("doneOn")
        @ColumnInfo(name = "DoneOn")
        open val doneOn: LocalDate? = null
    ) {

        fun getStatusString(): String = when (status) {
            null -> "None"
            true -> "Positive"
            false -> "Negative"
        }

        data class RepeatHeat(
            @Ignore
            override val expectedOn: LocalDate,

            @Ignore
            override val status: Boolean? = null,

            @Ignore
            override val doneOn: LocalDate? = null
        ) : BreedingEvent(expectedOn, status, doneOn)

        data class PregnancyCheck(
            @Ignore
            override val expectedOn: LocalDate,

            @Ignore
            override val status: Boolean? = null,

            @Ignore
            override val doneOn: LocalDate? = null
        ) : BreedingEvent(expectedOn, status, doneOn)

        data class DryOff(
            @Ignore
            override val expectedOn: LocalDate,

            @Ignore
            override val status: Boolean? = null,

            @Ignore
            override val doneOn: LocalDate? = null
        ) : BreedingEvent(expectedOn, status, doneOn)

        data class Calving(
            @Ignore
            override val expectedOn: LocalDate,

            @Ignore
            override val status: Boolean? = null,

            @Ignore
            override val doneOn: LocalDate? = null
        ) : BreedingEvent(expectedOn, status, doneOn)
    }
}