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
     * Shift in which milk was sold in a day.
     */
    @SerializedName("shift")
    @ColumnInfo(name = "shift")
    val shift: Shift,

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
    val amount: Float,

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
        set(value) = if (!value.isBlank()) {
            field = value
        } else {
            throw IllegalArgumentException("Milk id is blank")
        }

    sealed class Shift(val displayName: String) {
        companion object {
            /**
             * All instances of [Shift].
             */
            val INSTANCES: Map<String, Shift> by lazy {
                mapOf(
                    Morning.displayName to Morning,
                    Evening.displayName to Evening
                )
            }
        }

        object Morning : Shift("Morning") {
            override fun toString(): String = displayName
        }

        object Evening : Shift("Evening") {
            override fun toString(): String = displayName
        }
    }

    sealed class MilkOf(val displayName: String) {
        abstract val firstCharacter: Char

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
            override val firstCharacter: Char
                get() = displayName.first()

            override fun toString(): String = displayName
        }

        object Buffalo : MilkOf("Buffalo") {
            override val firstCharacter: Char
                get() = displayName.first()

            override fun toString(): String = displayName
        }
    }

    /**
     * The source of [Milk] data.
     */
    sealed class Source(open val SENDER_ADDRESS: String) {
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
        sealed class Sms(override val SENDER_ADDRESS: String) : Source(SENDER_ADDRESS) {
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

            object BGAMAMCS : Sms("BGAMAMCS")
        }

        /** The source of the milk data manual. */
        object Manual: Source("MANUAL")
    }
}