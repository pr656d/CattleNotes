package com.pr656d.cattlenotes.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pr656d.cattlenotes.model.Animal

/**]
 * This class represents [Animal] data to store in database.
 */
@Entity(tableName = "animals")
data class AnimalEntity(

    /**
     * Unique string identifying animal.
     */
    @PrimaryKey
    @SerializedName("tagNumber")
    @ColumnInfo(name = "tagNumber")
    val tagNumber: String,

    /**
     * Animal name. Name can be null.
     */
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    /**
     * Animal type [Animal.AnimalType] as string e.g. cow, buffalo etc.
     */
    @SerializedName("type")
    @ColumnInfo(name = "type")
    val type: String,

    /**
     * Animal breed [Animal.AnimalBreed] as string e.g. HF, Gir etc.
     */
    @SerializedName("breed")
    @ColumnInfo(name = "breed")
    val breed: String? = null,

    /**
     * Animal group [Animal.AnimalGroup] as string e.g. Dry, Milking etc.
     */
    @SerializedName("group")
    @ColumnInfo(name = "group")
    val group: String? = null,

    /**
     * Animal calving count of integer type.
     * Counted on basis of pregnancy cycle completed successfully.
     */
    @SerializedName("calving")
    @ColumnInfo(name = "calving")
    val calving: Int = 0,

    /**
     * Artificial Insemination date in millis as String.
     */
    @SerializedName("ai_date")
    @ColumnInfo(name = "ai_date")
    val aiDate: String?,

    /**
     * Depends on AI date.
     */
    @SerializedName("repeat_heat_date")
    @ColumnInfo(name = "repeat_heat_date")
    val repeatHeatDate: String?,

    /**
     * Depends on repeat heat status.
     */
    @SerializedName("pregnancy_check_date")
    @ColumnInfo(name = "pregnancy_check_date")
    val pregnancyCheckDate: String?,

    /**
     * Depends on pregnancy check status.
     */
    @SerializedName("dry_off_date")
    @ColumnInfo(name = "dry_off_date")
    val dryOffDate: String?
)