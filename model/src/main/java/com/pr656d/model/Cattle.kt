package com.pr656d.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

@Entity(tableName = "cattleList")
data class Cattle(
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
    val breed: Breed,

    @SerializedName("group")
    @ColumnInfo(name = "group")
    val group: Group,

    @SerializedName("lactation")
    @ColumnInfo(name = "lactation")
    val lactation: Long,

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

    @PrimaryKey
    @SerializedName("id")
    var id: String = ""
        set(value) = if (!value.isBlank()) {
            field = value
        } else {
            throw IllegalArgumentException("Cattle id is blank")
        }

    enum class Group(val displayName: String) {
        HEIFER("Heifer"),
        MILKING("Milking"),
        DRY("Dry")
    }

    enum class Breed(val displayName: String) {
        HF("HF"),
        JERSEY("Jersey"),
        GIR("Gir"),
        KANKREJ("Kankrej"),
        SHAHIVAL("Shahival")
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