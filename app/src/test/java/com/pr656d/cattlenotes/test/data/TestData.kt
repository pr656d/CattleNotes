package com.pr656d.cattlenotes.test.data

import com.pr656d.cattlenotes.data.model.Animal
import com.pr656d.cattlenotes.data.model.Cattle
import java.util.*

object TestData {

    val cattle1 = Cattle(
        tagNumber = 1, name = "Janu", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.MILKING, lactation = 3,
        homeBorn = false, purchaseAmount = 85000, purchaseDate = Date(1582806700),
        dateOfBirth = Date(1551270660), parent = null
    )

    val cattle2 = Cattle(
        tagNumber = 2, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.DRY, lactation = 1,
        homeBorn = true, purchaseAmount = null, purchaseDate = Date(1562606700),
        dateOfBirth = Date(1562606700), parent = 1
    )

    val cattle3 = Cattle(
        tagNumber = 872634658165, name = "Sita", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.HEIFER, lactation = 4,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = null
    )

    val cattle4 = Cattle(
        tagNumber = 243287657216, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.GIR, group = Cattle.Group.MILKING, lactation = 0,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = null
    )

    val cattle5 = Cattle(
        tagNumber = 5, name = "Lakshmi", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.DRY, lactation = 1,
        homeBorn = false, purchaseAmount = null, purchaseDate = Date(1562606700),
        dateOfBirth = Date(1562606700), parent = null
    )

    val cattleList = listOf(cattle1, cattle2, cattle3, cattle4, cattle5)

}