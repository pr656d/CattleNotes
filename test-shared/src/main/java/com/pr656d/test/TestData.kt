package com.pr656d.test

import com.pr656d.model.AnimalType
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInseminationInfo
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.BreedingWithCattle
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
        type = AnimalType.Cow, breed = Cattle.Breed.HF, group = Cattle.Group.Milking, lactation = 3,
        homeBorn = false, purchaseAmount = 85000, purchaseDate = LocalDate.ofEpochDay(1582806700),
        dateOfBirth = LocalDate.ofEpochDay(1551270660), parent = null
    ).apply { id = UUID.randomUUID().toString() }

    val cattle2 = Cattle(
        tagNumber = 2, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = Cattle.Breed.HF, group = Cattle.Group.Dry, lactation = 1,
        homeBorn = true, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = cattle1.id
    ).apply { id = UUID.randomUUID().toString() }

    val cattle3 = Cattle(
        tagNumber = 872634658165, name = "Sita", image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = Cattle.Breed.HF, group = Cattle.Group.Heifer, lactation = 4,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = cattle1.id
    ).apply { id = UUID.randomUUID().toString() }

    val cattle4 = Cattle(
        tagNumber = 243287657216, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = Cattle.Breed.GIR, group = Cattle.Group.Milking, lactation = 0,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = cattle2.id
    ).apply { id = UUID.randomUUID().toString() }

    val cattle5 = Cattle(
        tagNumber = 5, name = "Lakshmi", image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = Cattle.Breed.HF, group = Cattle.Group.Dry, lactation = 1,
        homeBorn = false, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = null
    ).apply { id = UUID.randomUUID().toString() }

    val cattleList = listOf(cattle1, cattle2, cattle3, cattle4, cattle5)


    /** Breeding Cycle */

    // Initial breeding
    val breedingInitial = Breeding(
        cattleId = cattle1.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now())
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative
    val breedingRepeatHeatNegative = Breeding(
        cattleId = cattle2.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status positive
    val breedingRepeatHeatPositive = Breeding(
        cattleId = cattle2.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = true
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status positive
    val breedingRHNegativePregnancyCheckPositive = Breeding(
        cattleId = cattle3.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = true
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status none
    val breedingRHNegativePregnancyCheckNone = Breeding(
        cattleId = cattle3.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = null
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status negative
    val breedingRHNegativePregnancyCheckNegative = Breeding(
        cattleId = cattle3.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = false
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status positive, dry off status positive
    val breedingRHNegativePCPositiveDryOffPositive = Breeding(
        cattleId = cattle4.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = true
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now()),
            status = true
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status positive, dry off status positive
    // Calving status positive
    val breedingRHNegativePCPositiveDOPositiveCalvingPositive = Breeding(
        cattleId = cattle4.id,
        artificialInsemination = ArtificialInseminationInfo(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeat_heat = BreedingEvent(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancy_check = BreedingEvent(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = true
        ),
        dry_off = BreedingEvent(
            expectedOn = getExpectedDryOffDate(LocalDate.now()),
            status = true
        ),
        calving_ = BreedingEvent(
            expectedOn = getExpectedCalvingDate(LocalDate.now()),
            status = true
        )
    )

    val breedingList = listOf(breedingInitial, breedingRepeatHeatNegative, breedingRepeatHeatPositive)

    /** Breeding with Cattle */

    val breedingWithCattle1 = BreedingWithCattle(cattle1, breedingInitial)

    val breedingWithCattle2 = BreedingWithCattle(cattle2, breedingRepeatHeatNegative)

    val breedingWithCattle3 = BreedingWithCattle(cattle2, breedingRepeatHeatPositive)

    val breedingWithCattle4 = BreedingWithCattle(cattle3, breedingRHNegativePregnancyCheckPositive)

    val breedingWithCattle5 = BreedingWithCattle(cattle3, breedingRHNegativePregnancyCheckNegative)

    val breedingWithCattle6 = BreedingWithCattle(cattle4, breedingRHNegativePCPositiveDryOffPositive)

    val breedingWithCattle7 = BreedingWithCattle(cattle4, breedingRHNegativePCPositiveDOPositiveCalvingPositive)

    val breedingWithCattleList = listOf(
        breedingWithCattle1, breedingWithCattle2, breedingWithCattle3, breedingWithCattle4,
        breedingWithCattle5, breedingWithCattle6, breedingWithCattle7
    )

    val completedBreedingWithCattleList = listOf(
        breedingWithCattle3, breedingWithCattle5, breedingWithCattle7
    )

    val validTimelineList = listOf(
        breedingWithCattle1, breedingWithCattle2,  breedingWithCattle4,
        breedingWithCattle6
    )

    val invalidTimelineList = completedBreedingWithCattleList
}