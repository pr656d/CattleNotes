package com.pr656d.model

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime

data class Milk(
    @SerializedName("dairyCode")
    @ColumnInfo(name = "dairyCode")
    val dairyCode: Int? = null,

    @SerializedName("customerId")
    @ColumnInfo(name = "customerId")
    val customerId: Int? = null,

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    val timestamp: ZonedDateTime,

    @SerializedName("shift")
    @ColumnInfo(name = "shift")
    val shift: Char? = null,

    @SerializedName("milkOf")
    @ColumnInfo(name = "milkOf")
    val milkOf: Char? = null,

    @SerializedName("quantity")
    @ColumnInfo(name = "quantity")
    val quantity: Float? = null,

    @SerializedName("fat")
    @ColumnInfo(name = "fat")
    val fat: Float? = null,

    @SerializedName("amount")
    @ColumnInfo(name = "amount")
    val amount: Float? = null,

    @SerializedName("totalQuantity")
    @ColumnInfo(name = "totalQuantity")
    val totalQuantity: Float? = null,

    @SerializedName("totalAmount")
    @ColumnInfo(name = "totalAmount")
    val totalAmount: Float? = null,

    @SerializedName("link")
    @ColumnInfo(name = "link")
    val link: String? = null
)