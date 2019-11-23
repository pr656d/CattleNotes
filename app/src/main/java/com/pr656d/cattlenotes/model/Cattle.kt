package com.pr656d.cattlenotes.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Describes a animal.
 */
@Parcelize
data class Cattle(
    val tagNumber: String,

    var name: String? = null,

    var type: String,

    var imageUrl: String? = null,

    var breed: String? = null,

    var group: String? = null,

    var calving: Int = 0,

    var dateOfBirth: String? = null,

    var aiDate: String? = null,

    var repeatHeatDate: String? = null,

    var pregnancyCheckDate: String? = null,

    var dryOffDate: String? = null,

    var calvingDate: String? = null,

    var purchaseAmount: Long? = 0,

    var purchaseDate: String? = null
): Parcelable