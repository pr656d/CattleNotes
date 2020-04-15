package com.pr656d.test

import com.pr656d.model.Animal
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInseminationInfo
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.Breeding.BreedingEvent.Type.*
import com.pr656d.model.Cattle
import com.pr656d.test.utils.BreedingUtil.getExpectedCalvingDate
import com.pr656d.test.utils.BreedingUtil.getExpectedDryOffDate
import com.pr656d.test.utils.BreedingUtil.getExpectedPregnancyCheckDate
import com.pr656d.test.utils.BreedingUtil.getExpectedRepeatHeatDate
import org.threeten.bp.LocalDate
import java.util.*

object TestData {

    /* Cattle */

    val cattle1 = Cattle(
        tagNumber = 1, name = "Janu", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.MILKING, lactation = 3,
        homeBorn = false, purchaseAmount = 85000, purchaseDate = LocalDate.ofEpochDay(1582806700),
        dateOfBirth = LocalDate.ofEpochDay(1551270660), parent = null
    ).apply { id = UUID.randomUUID().toString() }

    val cattle2 = Cattle(
        tagNumber = 2, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.DRY, lactation = 1,
        homeBorn = true, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = cattle1.id
    ).apply { id = UUID.randomUUID().toString() }

    val cattle3 = Cattle(
        tagNumber = 872634658165, name = "Sita", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.HEIFER, lactation = 4,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = cattle1.id
    ).apply { id = UUID.randomUUID().toString() }

    val cattle4 = Cattle(
        tagNumber = 243287657216, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.GIR, group = Cattle.Group.MILKING, lactation = 0,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = cattle2.id
    ).apply { id = UUID.randomUUID().toString() }

    val cattle5 = Cattle(
        tagNumber = 5, name = "Lakshmi", image = Cattle.Image(localPath = null, remotePath =  null),
        type = Animal.Type.COW, breed = Cattle.Breed.HF, group = Cattle.Group.DRY, lactation = 1,
        homeBorn = false, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = null
    ).apply { id = UUID.randomUUID().toString() }

    val cattleList = listOf(cattle1, cattle2, cattle3, cattle4, cattle5)


    /* Breeding Cycle */

    val breedingCycle1 = Breeding(
        cattleId = cattle1.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent(type = REPEAT_HEAT, expectedOn = getExpectedRepeatHeatDate(LocalDate.now())),
        pregnancyCheck = BreedingEvent(type = PREGNANCY_CHECK, expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())),
        dryOff = BreedingEvent(type = DRY_OFF, expectedOn = getExpectedDryOffDate(LocalDate.now())),
        calving = BreedingEvent(type = CALVING, expectedOn = getExpectedCalvingDate(LocalDate.now()))
    )

    val breedingList = listOf(breedingCycle1)
}