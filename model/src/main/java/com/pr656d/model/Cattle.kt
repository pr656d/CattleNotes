package com.pr656d.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

@Entity(tableName = "cattleList")
data class Cattle(
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerializedName("tagNumber")
    @ColumnInfo(name = "tagNumber", index = true)
    val tagNumber: Long,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    @SerializedName("image")
    @Embedded(prefix = "image")
    val image: Image? = null,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    val type: AnimalType,

    @SerializedName("breed")
    @ColumnInfo(name = "breed")
    val breed: String,

    @SerializedName("group")
    @ColumnInfo(name = "group")
    val group: Group?,

    @SerializedName("lactation")
    @ColumnInfo(name = "lactation")
    val lactation: Long?,

    @SerializedName("homeBorn")
    @ColumnInfo(name = "homeBorn")
    val homeBorn: Boolean = false,

    @SerializedName("purchaseAmount")
    @ColumnInfo(name = "purchaseAmount")
    val purchaseAmount: Long? = null,

    @SerializedName("purchasedOn")
    @ColumnInfo(name = "purchasedOn")
    val purchaseDate: LocalDate? = null,

    @SerializedName("dateOfBirth")
    @ColumnInfo(name = "dateOfBirth")
    val dateOfBirth: LocalDate? = null,

    @SerializedName("parentId")
    @ColumnInfo(name = "parentId")
    val parent: String? = null
) {
    sealed class Group(val displayName: String) {
        companion object {
            /**
             * All instances of [Group].
             */
            val INSTANCES: Map<String, Group> by lazy {
                mapOf(
                    Milking.displayName to Milking,
                    Dry.displayName to Dry,
                    Heifer.displayName to Heifer
                )
            }
        }

        object Milking : Group("Milking") {
            override fun toString(): String = displayName
        }

        object Dry : Group("Dry") {
            override fun toString(): String = displayName
        }

        object Heifer : Group("Heifer") {
            override fun toString(): String = displayName
        }
    }

    data class Image(
        @SerializedName("localPath")
        @ColumnInfo(name = "LocalPath")
        val localPath: String? = null,

        @SerializedName("remotePath")
        @ColumnInfo(name = "RemotePath")
        val remotePath: String? = null
    )
}