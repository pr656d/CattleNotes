package com.pr656d.test

import com.pr656d.model.*
import com.pr656d.model.Breeding.ArtificialInsemination
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.test.utils.BreedingUtil.getExpectedCalvingDate
import com.pr656d.test.utils.BreedingUtil.getExpectedDryOffDate
import com.pr656d.test.utils.BreedingUtil.getExpectedPregnancyCheckDate
import com.pr656d.test.utils.BreedingUtil.getExpectedRepeatHeatDate
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*

object TestData {

    /* Cattle */

    val cattle1 = Cattle(
        id = UUID.randomUUID().toString(),
        tagNumber = 1, name = "Janu", image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = "HF", group = Cattle.Group.Milking, lactation = 3,
        homeBorn = false, purchaseAmount = 85000, purchaseDate = LocalDate.ofEpochDay(1582806700),
        dateOfBirth = LocalDate.ofEpochDay(1551270660), parent = null
    )

    val cattle2 = Cattle(
        id = UUID.randomUUID().toString(),
        tagNumber = 2, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = "HF", group = Cattle.Group.Dry, lactation = 1,
        homeBorn = true, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = cattle1.id
    )

    val cattle3 = Cattle(
        id = UUID.randomUUID().toString(),
        tagNumber = 872634658165, name = "Sita", image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = "HF", group = Cattle.Group.Heifer, lactation = 4,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = cattle1.id
    )

    val cattle4 = Cattle(
        id = UUID.randomUUID().toString(),
        tagNumber = 243287657216, name = null, image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = "Gir", group = Cattle.Group.Milking, lactation = 0,
        homeBorn = false, purchaseAmount = null, purchaseDate = null,
        dateOfBirth = null, parent = cattle2.id
    )

    val cattle5 = Cattle(
        id = UUID.randomUUID().toString(),
        tagNumber = 5, name = "Lakshmi", image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Cow, breed = "HF", group = Cattle.Group.Dry, lactation = 1,
        homeBorn = false, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = cattle2.id
    )

    val cattleBull = Cattle(
        id = UUID.randomUUID().toString(),
        tagNumber = 5, name = "Lakshmi", image = Cattle.Image(localPath = null, remotePath =  null),
        type = AnimalType.Bull, breed = "HF", group = null, lactation = null,
        homeBorn = false, purchaseAmount = null, purchaseDate = LocalDate.ofEpochDay(1562606700),
        dateOfBirth = LocalDate.ofEpochDay(1562606700), parent = cattle2.id
    )

    val cattleList = listOf(cattle1, cattle2, cattle3, cattle4, cattle5, cattleBull)


    /** Breeding Cycle */

    // Initial breeding
    val breedingInitial = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle1.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now())
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative
    val breedingRepeatHeatNegative = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle1.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status positive
    val breedingRepeatHeatPositive = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle2.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = true
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now())
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status positive
    val breedingRHNegativePregnancyCheckPositive = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle3.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = true
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status none
    val breedingRHNegativePregnancyCheckNone = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle3.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = null
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status negative
    val breedingRHNegativePregnancyCheckNegative = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle3.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = false
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now())
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status positive, dry off status positive
    val breedingRHNegativePCPositiveDryOffPositive = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle4.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = true
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now()),
            status = true
        ),
        calving = BreedingEvent.Calving(
            expectedOn = getExpectedCalvingDate(LocalDate.now())
        )
    )

    // Repeat heat status negative, pregnancy check status positive, dry off status positive
    // Calving status positive
    val breedingRHNegativePCPositiveDOPositiveCalvingPositive = Breeding(
        id = UUID.randomUUID().toString(),
        cattleId = cattle4.id,
        artificialInsemination = ArtificialInsemination(
            date = LocalDate.now(),
            didBy = null,
            bullName = null,
            strawCode = null
        ),
        repeatHeat = BreedingEvent.RepeatHeat(
            expectedOn = getExpectedRepeatHeatDate(LocalDate.now()),
            status = false
        ),
        pregnancyCheck = BreedingEvent.PregnancyCheck(
            expectedOn = getExpectedPregnancyCheckDate(LocalDate.now()),
            status = true
        ),
        dryOff = BreedingEvent.DryOff(
            expectedOn = getExpectedDryOffDate(LocalDate.now()),
            status = true
        ),
        calving = BreedingEvent.Calving(
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

    val milkMessage1 = """
            |Code-(268) 1047
            |Date-16/01/2020 06:41
            |Shift-M
            |Milk-C
            |Qty-36.5
            |Fat-3.8
            |Amt-1057.09
            |TQty-391.7
            |TAmt-11282.60
            |https://goo.gl/UY1HAC""".trimMargin()

    val milk1 = Milk(
        Milk.Source.Sms.BGAMAMCS,
        ZonedDateTime.of(2020, 1, 16, 6, 41, 0, 0, ZoneId.systemDefault()),
        Milk.Shift.Morning, Milk.MilkOf.Cow, 36.5f, 3.8f, 1057.09f,
        391.7f, 11282.60f, "https://goo.gl/UY1HAC"
    )

    val milk2 = Milk(
        Milk.Source.Sms.BGAMAMCS,
        ZonedDateTime.of(2020, 1, 16, 18, 32, 0, 0, ZoneId.systemDefault()),
        Milk.Shift.Morning, Milk.MilkOf.Cow, 36.5f, 3.8f, 1057.09f,
        391.7f, 11282.60f, "https://goo.gl/UY1HAC"
    )

    val milk3 = Milk(
        Milk.Source.Sms.BGAMAMCS,
        ZonedDateTime.of(2020, 1, 17, 6, 47, 0, 0, ZoneId.systemDefault()),
        Milk.Shift.Morning, Milk.MilkOf.Cow, 36.5f, 3.8f, 1057.09f,
        391.7f, 11282.60f, "https://goo.gl/UY1HAC"
    )

    val milk4 = Milk(
        Milk.Source.Sms.BGAMAMCS,
        ZonedDateTime.of(2020, 1, 17, 19, 15, 0, 0, ZoneId.systemDefault()),
        Milk.Shift.Morning, Milk.MilkOf.Cow, 36.5f, 3.8f, 1057.09f,
        391.7f, 11282.60f, "https://goo.gl/UY1HAC"
    )

    val milkList = listOf(milk1, milk2, milk3, milk4)
}