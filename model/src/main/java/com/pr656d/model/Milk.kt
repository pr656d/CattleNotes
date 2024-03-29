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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime

/**
 * Represents Milking data. Some fields may depend on data given in SMS while using
 * AMC (Automatic Milking Collection) feature.
 */
@Entity(tableName = "milkList")
data class Milk(
    /**
     * The source of this data.
     */
    @SerializedName("source")
    @ColumnInfo(name = "source")
    val source: Source,

    /**
     * Timestamp when this milk was sold.
     * This field is unique.
     */
    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    val timestamp: ZonedDateTime,

    /**
     * Holds whose milk is this (Animal type).
     */
    @SerializedName("milkOf")
    @ColumnInfo(name = "milkOf")
    val milkOf: MilkOf,

    /**
     * The quantity of milk.
     */
    @SerializedName("quantity")
    @ColumnInfo(name = "quantity")
    val quantity: Float,

    /**
     * The fat of milk.
     */
    @SerializedName("fat")
    @ColumnInfo(name = "fat")
    val fat: Float,

    /**
     * The price of milk.
     */
    @SerializedName("amount")
    @ColumnInfo(name = "amount")
    val amount: Float? = null,

    /**
     * The total quantity of milk so far.
     * This data maybe provided by company by SMS.
     */
    @SerializedName("totalQuantity")
    @ColumnInfo(name = "totalQuantity")
    val totalQuantity: Float? = null,

    /**
     * The total price of milk so far.
     * This data maybe provided by company by SMS.
     */
    @SerializedName("totalAmount")
    @ColumnInfo(name = "totalAmount")
    val totalAmount: Float? = null,

    /**
     * The link if any in SMS to view in company's portal.
     */
    @SerializedName("link")
    @ColumnInfo(name = "link")
    val link: String? = null
) {
    /**
     * [id] will be ignored in [equals]. But we have unique [timestamp] also.
     * Intentionally ignoring [id] field because [Milk] can be generated from different
     * sources and we want to compare them. Then only thing unique in them is [timestamp].
     *
     * Properties declared in the class body are excluded for auto generated functions.
     * https://kotlinlang.org/docs/reference/data-classes.html?_ga=2.24250169.2023426078.1588739953-1170053916.1568118581#properties-declared-in-the-class-body
     */
    @PrimaryKey
    @SerializedName("id")
    var id: String = ""
        get() =
            if (field.isNotBlank())
                field
            else
                throw IllegalArgumentException("Milk id is blank")

    sealed class MilkOf(val displayName: String) {
        companion object {
            /**
             * All sources of [MilkOf].
             */
            val INSTANCES: Map<String, MilkOf> by lazy {
                mapOf(
                    Cow.displayName to Cow,
                    Buffalo.displayName to Buffalo
                )
            }
        }

        object Cow : MilkOf("Cow") {
            override fun toString(): String = displayName
        }

        object Buffalo : MilkOf("Buffalo") {
            override fun toString(): String = displayName
        }
    }

    /**
     * The source of [Milk] data.
     */
    sealed class Source(
        @SerializedName("sourceSenderAddress")
        open val SENDER_ADDRESS: String
    ) {
        companion object {
            /**
             * All instances of [Source].
             */
            val INSTANCES: Map<String, Source> by lazy {
                Sms.INSTANCES + mapOf(
                    Manual.SENDER_ADDRESS to Manual
                )
            }
        }

        /** The source of the milk data is SMS. */
        sealed class Sms(
            @SerializedName("smsSenderAddress")
            override val SENDER_ADDRESS: String,

            @SerializedName("smsOriginatingAddress")
            val ORIGINATING_ADDRESS: String
        ) : Source(SENDER_ADDRESS) {
            /**
             * Valid milk SMS sender address list.
             * Register new parser here.
             */
            companion object {
                /**
                 * All instances of [Source] extended by [Sms].
                 */
                val INSTANCES: Map<String, Sms> by lazy {
                    mapOf(
                        BGAMAMCS.SENDER_ADDRESS to BGAMAMCS
                    )
                }
            }

            object BGAMAMCS : Sms("BGAMAMCS", "BG-AMAMCS")
        }

        /** The source of the milk data manual. */
        object Manual: Source("MANUAL")
    }
}