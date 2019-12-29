package com.pr656d.cattlenotes.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

abstract class Animal(
    @PrimaryKey
    @SerializedName("tag_number")
    @ColumnInfo(name = "tag_number", index = true)
    val tagNumber: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String?,

    @SerializedName("image")
    @Embedded(prefix = "image_")
    val image: Image?,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    val type: Type,

    @SerializedName("home_birth")
    @ColumnInfo(name = "home_birth")
    val homeBirth: Boolean,

    @SerializedName("purchase_amount")
    @ColumnInfo(name = "purchase_amount")
    val purchaseAmount: Long?,

    @SerializedName("purchased_on")
    @ColumnInfo(name = "purchased_on")
    val purchasedOn: Date?,

    @SerializedName("date_of_birth")
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Date?,

    @SerializedName("parent")
    @ColumnInfo(name = "parent")
    val parent: String?
) {
    enum class Type(val displayName: String) {
        COW("Cow"),
        BUFFALO("Buffalo"),
        BULL("Bull")
    }

    data class Image(
        @SerializedName("local_path")
        @ColumnInfo(name = "local_path")
        val localPath: String? = null,

        @SerializedName("remote_path")
        @ColumnInfo(name = "remote_path")
        val remotePath: String? = null
    )
}