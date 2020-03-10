package com.pr656d.cattlenotes.test.data

import com.pr656d.cattlenotes.data.model.Animal
import com.pr656d.cattlenotes.data.model.Cattle
import org.threeten.bp.LocalDate

object TestData {

    val cattle1 = Cattle(
        tagNumber = 1, name = "Janu", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.MILKING, lactation = 3,
        homeBorn = false, purchaseAmount = 85000, purchaseDate = LocalDate.ofEpochDay(1582806700),
        dateOfBirth = LocalDate.ofEpochDay(1551270660), parent = null
    ).apply { id = 1 }

    val cattle2 = Cattle(
        tagNumber = 2, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.DRY, lactation = 1,
        homeBorn = true, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = cattle1.tagNumber
    ).apply { id = 2 }

    val cattle3 = Cattle(
        tagNumber = 872634658165, name = "Sita", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.HEIFER, lactation = 4,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = null
    ).apply { id = 3 }

    val cattle4 = Cattle(
        tagNumber = 243287657216, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.GIR, group = Cattle.Group.MILKING, lactation = 0,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = null
    ).apply { id = 4 }

    val cattle5 = Cattle(
        tagNumber = 5, name = "Lakshmi", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.DRY, lactation = 1,
        homeBorn = false, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = null
    ).apply { id = 6 }

    val cattleList = listOf(cattle1, cattle2, cattle3, cattle4, cattle5)

}