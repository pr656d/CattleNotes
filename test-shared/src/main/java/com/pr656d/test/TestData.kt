package com.pr656d.test

import com.pr656d.model.Animal
import com.pr656d.model.BreedingCycle
import com.pr656d.model.BreedingCycle.ArtificialInseminationInfo
import com.pr656d.model.BreedingCycle.BreedingEvent
import com.pr656d.model.Cattle
import com.pr656d.test.utils.BreedingUtil.getExpectedCalvingDate
import com.pr656d.test.utils.BreedingUtil.getExpectedDryOffDate
import com.pr656d.test.utils.BreedingUtil.getExpectedPregnancyCheckDate
import com.pr656d.test.utils.BreedingUtil.getExpectedRepeatHeatDate
import org.threeten.bp.LocalDate

object TestData {

    /* Cattle */

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
    ).apply { id = 5 }

    val cattleList = listOf(cattle1, cattle2, cattle3, cattle4, cattle5)


    /* Breeding Cycle */

    val breedingCycle1 = BreedingCycle(
        cattleId = cattle1.id,
        isActive = false,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent(getExpectedRepeatHeatDate(LocalDate.now())),
        pregnancyCheck = BreedingEvent(getExpectedPregnancyCheckDate(LocalDate.now())),
        dryOff = BreedingEvent(getExpectedDryOffDate(LocalDate.now())),
        calving = BreedingEvent(getExpectedCalvingDate(LocalDate.now()))
    )

    val breedingList = listOf(breedingCycle1)
}