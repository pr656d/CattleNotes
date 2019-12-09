package com.pr656d.cattlenotes.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pr656d.cattlenotes.model.Cattle

/**]
 * This class represents [Cattle] data to store in database.
 */
@Entity(tableName = "cattle_list")
data class CattleEntity(

    /**
     * Unique string identifying animal.
     */
    @PrimaryKey
    @SerializedName("tagNumber")
    @ColumnInfo(name = "tagNumber")
    val tagNumber: Long,

    /**
     * Cattle name. Name can be null.
     */
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    /**
     * Cattle type as string e.g. cow, buffalo etc.
     */
    @SerializedName("type")
    @ColumnInfo(name = "type")
    val type: String,

    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    /**
     * Cattle breed as string e.g. HF, Gir etc.
     */
    @SerializedName("breed")
    @ColumnInfo(name = "breed")
    val breed: String? = null,

    /**
     * Cattle group as string e.g. Dry, Milking etc.
     */
    @SerializedName("group")
    @ColumnInfo(name = "group")
    val group: String? = null,

    /**
     * Cattle calving count of integer type.
     * Counted on basis of pregnancy cycle completed successfully.
     */
    @SerializedName("calving")
    @ColumnInfo(name = "calving")
    val calving: Int = 0,

    @SerializedName("date_of_birth")
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String? = null,

    /**
     * Artificial Insemination date as String.
     */
    @SerializedName("ai_date")
    @ColumnInfo(name = "ai_date")
    val aiDate: String? = null,

    /**
     * Depends on AI date.
     */
    @SerializedName("repeat_heat_date")
    @ColumnInfo(name = "repeat_heat_date")
    val repeatHeatDate: String? = null,

    /**
     * Depends on repeat heat status.
     */
    @SerializedName("pregnancy_check_date")
    @ColumnInfo(name = "pregnancy_check_date")
    val pregnancyCheckDate: String? = null,

    /**
     * Depends on pregnancy check status.
     */
    @SerializedName("dry_off_date")
    @ColumnInfo(name = "dry_off_date")
    val dryOffDate: String? = null,

    @SerializedName("calving_date")
    @ColumnInfo(name = "calving_date")
    val calvingDate: String? = null,

    @SerializedName("purchase_amount")
    @ColumnInfo(name = "purchase_amount")
    val purchaseAmount: Long? = null,

    @SerializedName("purchase_date")
    @ColumnInfo(name = "purchase_date")
    val purchaseDate: String? = null
)