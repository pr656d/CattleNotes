package com.pr656d.cattlenotes.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.ArtificialInseminationInfo
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
            cattleId = UUID.randomUUID().toString(),
            artificialInsemination = ArtificialInseminationInfo(aiDate, null, null, null),
            repeat_heat = BreedingEvent(
                expectedOn = BreedingUtil.getExpectedRepeatHeatDate(aiDate),
                status = repeatHeatStatus
            ),
            pregnancy_check = BreedingEvent(
                expectedOn = BreedingUtil.getExpectedPregnancyCheckDate(aiDate),
                status = pregnancyCheckStatus
            ),
            dry_off = BreedingEvent(
                expectedOn = BreedingUtil.getExpectedDryOffDate(aiDate),
                status = dryOffStatus
            ),
            calving_ = BreedingEvent(
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

        assertThat(breeding.repeatHeat, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun repeatHeatIsPositive_nextBreedingEventIsNull() {
        val breeding = createBreeding(repeatHeatStatus = true)

        assertNull(breeding.getNextBreedingEvent())
    }

    @Test
    fun repeatHeatIsNegative_nextBreedingEventIsPregnancyCheck() {
        val breeding = createBreeding(
            repeatHeatStatus = false
        )

        assertThat(breeding.pregnancyCheck, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun pregnancyCheckIsNull_nextBreedingEventIsPregnancyCheck() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = null
        )

        assertThat(breeding.pregnancyCheck, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun pregnancyCheckIsPositive_nextBreedingEventIsDryOff() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true
        )

        assertThat(breeding.dryOff, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun pregnancyCheckIsNegative_nextBreedingEventIsNull() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = false
        )

        assertNull(breeding.getNextBreedingEvent())
    }

    @Test
    fun dryOffIsNull_nextBreedingEventIsDryOff() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = null
        )

        assertThat(breeding.dryOff, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun dryOffIsPositive_nextBreedingEventIsCalving() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = true
        )

        assertThat(breeding.calving, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun calvingIsNull_nextBreedingEventIsCalving() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = true, calvingStatus = null
        )

        assertThat(breeding.calving, isEqualTo(breeding.getNextBreedingEvent()))
    }

    @Test
    fun calvingIsPositive_nextBreedingEventIsNull() {
        val breeding = createBreeding(
            repeatHeatStatus = false, pregnancyCheckStatus = true,
            dryOffStatus = true, calvingStatus = true
        )

        assertNull(breeding.getNextBreedingEvent())
    }
}