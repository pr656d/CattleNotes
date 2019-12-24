package com.pr656d.cattlenotes.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "cattle_list", inheritSuperIndices = true)
class Cattle(
    tagNumber: String,

    name: String? = null,

    image: Image? = null,

    type: Type = Type.COW,

    @SerializedName("breed")
    @ColumnInfo(name = "breed")
    val breed: Breed = Breed.HF,

    @SerializedName("group")
    @ColumnInfo(name = "group")
    val group: Group = Group.MILKING,

    homeBirth: Boolean = false,

    purchaseAmount: Long? = null,

    purchasedOn: Date? = null,

    dateOfBirth: Date? = null,

    parent: Long? = null
) : Animal(
    tagNumber, name, image, type, homeBirth, purchaseAmount, purchasedOn, dateOfBirth, parent
) {
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
}