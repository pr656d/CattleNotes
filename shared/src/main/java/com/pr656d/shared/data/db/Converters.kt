package com.pr656d.shared.data.db

import androidx.room.TypeConverter
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.model.Milk
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import com.pr656d.shared.utils.toZonedDateTime
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class Converters {
    @TypeConverter
    fun fromMillisToLocalDate(millis: Long?): LocalDate? = millis?.toLocalDate()

    @TypeConverter
    fun fromLocalDateToMillis(localDate: LocalDate?): Long? = localDate?.toLong()

    @TypeConverter
    fun fromMillisToZonedDateTime(millis: Long?): ZonedDateTime? = millis?.toZonedDateTime()

    @TypeConverter
    fun fromZonedDateTimeToMillis(zonedDateTime: ZonedDateTime): Long? =
        TimeUtils.toEpochMilli(zonedDateTime)

    @TypeConverter
    fun fromStringToAnimalType(value: String): AnimalType = AnimalType.INSTANCES[value]
        ?: throw IllegalArgumentException("Invalid String: Can not convert $value to Cattle.Type")

    @TypeConverter
    fun fromAnimalTypeToString(value: AnimalType): String = value.displayName

    @TypeConverter
    fun fromGroupToString(value: Cattle.Group?): String? = value?.displayName

    @TypeConverter
    fun fromStringToGroup(value: String?): Cattle.Group? = value?.let {
        Cattle.Group.INSTANCES[it]
            ?: throw IllegalArgumentException("Invalid String: Can not convert $value to Cattle.Group")
    }

    @TypeConverter
    fun fromMilkOfToString(value: Milk.MilkOf): String = value.displayName

    @TypeConverter
    fun fromStringToMilkOf(value: String): Milk.MilkOf = Milk.MilkOf.INSTANCES[value]
        ?: throw IllegalArgumentException("Invalid String: Can not convert $value to Milk.MilkOf")

    @TypeConverter
    fun fromShiftToString(value: Milk.Shift): String = value.displayName

    @TypeConverter
    fun fromStringToShift(value: String): Milk.Shift = Milk.Shift.INSTANCES[value]
        ?: throw IllegalArgumentException("Invalid String: Can not convert $value to Milk.Shift")

    @TypeConverter
    fun fromMilkSourceToString(value: Milk.Source): String = value.SENDER_ADDRESS

    @TypeConverter
    fun fromStringToMilkSource(value: String): Milk.Source = Milk.Source.INSTANCES[value]
        ?: throw IllegalArgumentException("Invalid String : Can not convert $value to Milk.Source")

    @TypeConverter
    fun fromImageToString(value: Cattle.Image?): String? = value?.let{
        "${it.localPath}/#SEPERATOR#/${it.remotePath}"
    }

    @TypeConverter
    fun fromStringToImage(value: String): Cattle.Image =
        Cattle.Image(
            localPath = value.split("/#SEPERATOR#/")[0],
            remotePath = value.split("/#SEPERATOR#/")[1]
        )
}
