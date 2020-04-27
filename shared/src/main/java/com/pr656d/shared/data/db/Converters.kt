package com.pr656d.shared.data.db

import androidx.room.TypeConverter
import com.pr656d.model.AnimalType
import com.pr656d.model.Cattle
import com.pr656d.shared.utils.toLocalDate
import com.pr656d.shared.utils.toLong
import org.threeten.bp.LocalDate

class Converters {
    @TypeConverter
    fun fromMillisToDate(millis: Long?): LocalDate? = millis?.toLocalDate()

    @TypeConverter
    fun fromDateToMillis(localDate: LocalDate?): Long? = localDate?.toLong()

    @TypeConverter
    fun fromStringToAnimalType(value: String): AnimalType = when(value) {
        AnimalType.Cow.displayName -> AnimalType.Cow
        AnimalType.Buffalo.displayName -> AnimalType.Buffalo
        AnimalType.Bull.displayName -> AnimalType.Bull
        else -> throw Exception("Invalid String: Can not convert $value to Cattle.Type")
    }

    @TypeConverter
    fun fromAnimalTypeToString(value: AnimalType): String = value.displayName

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
        Cattle.Group.Milking.displayName -> Cattle.Group.Milking
        Cattle.Group.Dry.displayName -> Cattle.Group.Dry
        Cattle.Group.Heifer.displayName -> Cattle.Group.Heifer
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
