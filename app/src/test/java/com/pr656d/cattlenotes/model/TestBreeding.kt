package com.pr656d.cattlenotes.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInsemination
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.shared.utils.BreedingUtil
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.*
import org.hamcrest.Matchers.equalTo as isEqualTo

class TestBreeding {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun createBreeding(
        repeatHeatStatus: Boolean? = null,
        pregnancyCheckStatus: Boolean? = null,
        dryOffStatus: Boolean? = null,
        calvingStatus: Boolean? = null
    ) : Breeding {
        val aiDate = LocalDate.now()

        return Breeding(
            id = UUID.randomUUID().toString(),
            cattleId = UUID.randomUUID().toString(),
            artificialInsemination = ArtificialInsemination(aiDate, null, null, null),
            repeatHeat = BreedingEvent.RepeatHeat(
                expectedOn = BreedingUtil.getExpectedRepeatHeatDate(aiDate),
                status = repeatHeatStatus
            ),
            pregnancyCheck = BreedingEvent.PregnancyCheck(
                expectedOn = BreedingUtil.getExpectedPregnancyCheckDate(aiDate),
                status = pregnancyCheckStatus
            ),
            dryOff = BreedingEvent.DryOff(
                expectedOn = BreedingUtil.getExpectedDryOffDate(aiDate),
                status = dryOffStatus
            ),
            calving = BreedingEvent.Calving(
                expectedOn = BreedingUtil.getExpectedCalvingDate(aiDate),
                status = calvingStatus
            )
        )
    }

    @Test
    fun repeatHeatIsNull_nextBreedingEventIsRepeatHeat() {
        val breeding = createBreeding(
            repeatHeatStatus = null
        )

        assertThat(breeding.repeatHeat, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun repeatHeatIsPositive_nextBreedingEventIsNull() {
        val breeding = createBreeding(repeatHeatStatus = true)

        assertNull(breeding.nextBreedingEvent)
    }

    @Test
    fun repeatHeatIsNegative_nextBreedingEventIsPregnancyCheck() {
        val breeding = createBreeding(
            repeatHeatStatus = false
        )

        assertThat(breeding.pregnancyCheck, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun pregnancyCheckIsNull_nextBreedingEventIsPregnancyCheck() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = null
        )

        assertThat(breeding.pregnancyCheck, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun pregnancyCheckIsPositive_nextBreedingEventIsDryOff() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true
        )

        assertThat(breeding.dryOff, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun pregnancyCheckIsNegative_nextBreedingEventIsNull() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = false
        )

        assertNull(breeding.nextBreedingEvent)
    }

    @Test
    fun dryOffIsNull_nextBreedingEventIsDryOff() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = null
        )

        assertThat(breeding.dryOff, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun dryOffIsPositive_nextBreedingEventIsCalving() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = true
        )

        assertThat(breeding.calving, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun calvingIsNull_nextBreedingEventIsCalving() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = true, calvingStatus = null
        )

        assertThat(breeding.calving, isEqualTo(breeding.nextBreedingEvent))
    }

    @Test
    fun calvingIsPositive_nextBreedingEventIsNull() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = true, calvingStatus = true
        )

        assertNull(breeding.nextBreedingEvent)
    }
}