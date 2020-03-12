package com.pr656d.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

@Entity(tableName = "cattle_list")
data class Cattle(
    @SerializedName("tag_number")
    @ColumnInfo(name = "tag_number", index = true)
    val tagNumber: Long,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    @SerializedName("image")
    @Embedded(prefix = "image_")
    val image: Image? = null,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    val type: Animal.Type,

    @SerializedName("breed")
    @ColumnInfo(name = "breed")
    val breed: Breed,

    @SerializedName("group")
    @ColumnInfo(name = "group")
    val group: Group,

    @SerializedName("lactation")
    @ColumnInfo(name = "lactation")
    val lactation: Int,

    @SerializedName("home_birth")
    @ColumnInfo(name = "home_birth")
    val homeBorn: Boolean = false,

    @SerializedName("purchase_amount")
    @ColumnInfo(name = "purchase_amount")
    val purchaseAmount: Long? = null,

    @SerializedName("purchased_on")
    @ColumnInfo(name = "purchased_on")
    val purchaseDate: LocalDate? = null,

    @SerializedName("date_of_birth")
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: LocalDate? = null,

    @SerializedName("parent")
    @ColumnInfo(name = "parent")
    val parent: Long? = null
) {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Long = 0

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
        @SerializedName("local_path")
        @ColumnInfo(name = "local_path")
        val localPath: String? = null,

        @SerializedName("remote_path")
        @ColumnInfo(name = "remote_path")
        val remotePath: String? = null
    )
}