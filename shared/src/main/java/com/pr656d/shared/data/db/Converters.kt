package com.pr656d.shared.data.db

import androidx.room.TypeConverter
import com.pr656d.model.Animal
import com.pr656d.model.Cattle
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.LocalDate

class Converters {
    @TypeConverter
    fun fromMillisToDate(millis: Long?): LocalDate? = millis?.let { TimeUtils.toLocalDate(it) }

    @TypeConverter
    fun fromDateToMillis(localDate: LocalDate?): Long? = localDate?.let { TimeUtils.toEpochMillis(it) }

    @TypeConverter
    fun fromStringToType(value: String): Animal.Type = when(value) {
        Animal.Type.COW.displayName -> Animal.Type.COW
        Animal.Type.BUFFALO.displayName -> Animal.Type.BUFFALO
        Animal.Type.BULL.displayName -> Animal.Type.BULL
        else -> throw Exception("Invalid String: Can not convert $value to Cattle.Type")
    }

    @TypeConverter
    fun fromTypeToString(value: Animal.Type): String = value.displayName

    @TypeConverter
    fun fromStringToBreed(value: String): Cattle.Breed = when (value) {
        Cattle.Breed.HF.displayName -> Cattle.Breed.HF
        Cattle.Breed.GIR.displayName -> Cattle.Breed.GIR
        Cattle.Breed.JERSEY.displayName -> Cattle.Breed.JERSEY
        Cattle.Breed.KANKREJ.displayName -> Cattle.Breed.KANKREJ
        Cattle.Breed.SHAHIVAL.displayName -> Cattle.Breed.SHAHIVAL
        else -> throw Exception("Invalid String: Can not convert $value to Cattle.Breed")
    }

    @TypeConverter
    fun fromBreedToString(value: Cattle.Breed): String = value.displayName

    @TypeConverter
    fun fromGroupToString(value: Cattle.Group): String = value.displayName

    @TypeConverter
    fun fromStringToGroup(value: String): Cattle.Group = when (value) {
        Cattle.Group.MILKING.displayName -> Cattle.Group.MILKING
        Cattle.Group.DRY.displayName -> Cattle.Group.DRY
        Cattle.Group.HEIFER.displayName -> Cattle.Group.HEIFER
        else -> throw Exception("Invalid String: Can not convert $value to Cattle.Group")
    }

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
