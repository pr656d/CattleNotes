/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.breeding.FakeBreedingRepository
import com.pr656d.cattlenotes.test.fakes.data.cattle.FakeCattleRepository
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.breeding.addedit.AddBreedingUseCase
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.breeding.detail.GetBreedingByIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [AddEditBreedingViewModel].
 */
@ExperimentalCoroutinesApi
class AddEditBreedingViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private val fakeCattleRepository = object : FakeCattleRepository() {
        override fun getCattleById(id: String): Flow<Cattle?> =
            flowOf(TestData.cattleList.firstOrNull { it.id == id })
    }

    private val fakeBreedingRepository = object : FakeBreedingRepository() {
        override fun getBreedingById(breedingId: String): Flow<Breeding?> =
            flowOf(TestData.breedingList.firstOrNull { it.id == breedingId })
    }

    private fun getBreedingBehaviour(
        breedingRepository: BreedingRepository,
        cattleRepository: CattleRepository
    ): BreedingBehaviour {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return BreedingBehaviourImpl(
            BreedingUiImplDelegate(
                GetCattleByIdUseCase(cattleRepository, coroutineDispatcher),
                GetBreedingByIdUseCase(breedingRepository, coroutineDispatcher)
            ) as BreedingUiDelegate
        )
    }

    private fun createAddEditBreedingViewModel(
        breedingRepository: BreedingRepository = fakeBreedingRepository,
        cattleRepository: CattleRepository = fakeCattleRepository
    ): AddEditBreedingViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return AddEditBreedingViewModel(
            breedingBehaviour = getBreedingBehaviour(breedingRepository, cattleRepository),
            addBreedingUseCase = AddBreedingUseCase(breedingRepository, coroutineDispatcher),
            updateBreedingUseCase = UpdateBreedingUseCase(breedingRepository, coroutineDispatcher)
        ).apply {
            initializeLiveData()
            observeUnobserved()
        }
    }

    /**
     * For testing we need to initialize them with initial value
     * as they are [MediatorLiveData].
     */
    private fun AddEditBreedingViewModel.initializeLiveData() {
        /*  AI  */
        aiDate.value = null
        didBy.value = null
        bullName.value = null
        strawCode.value = null
        /*  Repeat heat  */
        repeatHeatStatus.value = null
        repeatHeatDoneOn.value = null
        /*  Pregnancy check  */
        pregnancyCheckStatus.value = null
        pregnancyCheckDoneOn.value = null
        /*  Dry off  */
        dryOffStatus.value = null
        dryOffDoneOn.value = null
        /*  Calving  */
        calvingStatus.value = null
        calvingDoneOn.value = null
    }

    private fun AddEditBreedingViewModel.observeUnobserved() {
        cattle.observeForever { }
        oldBreeding.observeForever { }
        /*  AI  */
        aiDate.observeForever { }
        didBy.observeForever { }
        bullName.observeForever { }
        strawCode.observeForever { }
        /*  Repeat heat  */
        repeatHeatExpectedOn.observeForever { }
        repeatHeatStatus.observeForever { }
        repeatHeatDoneOn.observeForever { }
        /*  Pregnancy check  */
        pregnancyCheckExpectedOn.observeForever { }
        pregnancyCheckStatus.observeForever { }
        pregnancyCheckDoneOn.observeForever { }
        /*  Dry off  */
        dryOffExpectedOn.observeForever { }
        dryOffStatus.observeForever { }
        dryOffDoneOn.observeForever { }
        /*  Calving  */
        calvingExpectedOn.observeForever { }
        calvingStatus.observeForever { }
        calvingDoneOn.observeForever { }
    }

    @Test
    fun saveCalledButAiDateNotAvailable_hasAiDateFalse() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Call save
        viewModel.save()

        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertFalse(hasAiDate)
    }

    @Test
    fun saveCalledWithAiDateIsAvailable_hasAiDateTrue() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set AI date
        viewModel.aiDate.value = LocalDate.now()

        // Call save
        viewModel.save()

        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)
    }

    @Test
    fun saveCalled_navigateUpOnSuccess() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set cattle
        viewModel.setCattle(TestData.cattle1.id)

        // Set AI date
        viewModel.aiDate.value = LocalDate.now()

        // Call save
        viewModel.save()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    /**
     * When aiDate is not set and back is pressed. Navigate up with out back confirmation.
     */
    @Test
    fun onBackPressedCalledAiDateNotSet_navigateUp() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Call onBackPressed
        viewModel.onBackPressed()

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    /**
     * When aiDate is set and back is pressed. Navigate up with back confirmation.
     */
    @Test
    fun onBackPressedCalledAiDateIsSet_showBackConfirmation() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        // Call onBackPressed
        viewModel.onBackPressed()

        val showBackConfirmationDialog =
            LiveDataTestUtil.getValue(viewModel.showBackConfirmationDialog)
        assertThat(Unit, isEqualTo(showBackConfirmationDialog?.getContentIfNotHandled()))
    }

    /**
     * When aiDate is set and back is pressed with back confirmation as true navigate up.
     */
    @Test
    fun onBackPressedCalledAiDateIsSetAndBackConfirmationIsTrue_navigateUp() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            // Call onBackPressed with out back confirmation
            viewModel.onBackPressed()

            // Check we have showed back confirmation earlier
            val showBackConfirmationDialog =
                LiveDataTestUtil.getValue(viewModel.showBackConfirmationDialog)
            assertThat(Unit, isEqualTo(showBackConfirmationDialog?.peekContent()))

            // Call onBackPressed with back confirmation
            viewModel.onBackPressed(backConfirmation = true)

            val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
            assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
        }

    @Test
    fun saveCalled_showMessageOnError() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel(
            object : FakeBreedingRepository() {
                override fun getBreedingById(breedingId: String): Flow<Breeding?> =
                    flowOf(TestData.breedingList.firstOrNull { it.id == breedingId })

                override suspend fun addBreeding(breeding: Breeding): Long {
                    throw Exception("Error!")
                }

                override suspend fun updateBreeding(breeding: Breeding) {
                    throw Exception("Error!")
                }
            }
        )

        // Set cattle
        viewModel.setCattle(TestData.cattle1.id)

        // Set AI date
        viewModel.aiDate.value = LocalDate.now()

        // Call save
        viewModel.save()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    /* Add breeding */

    @Test
    fun oldBreedingIsNotAvailable_editingIsFalse() {
        val viewModel = createAddEditBreedingViewModel()

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertFalse(editing)
    }

    /* Edit breeding */

    @Test
    fun oldBreedingIsAvailable_editingIsTrue() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set old breeding
        viewModel.setBreeding(TestData.breedingInitial.id)

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertTrue(editing)
    }

    @Test
    fun editingExistingBreedingCycleBindDataCalled_verifyBindingOfData() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            val actualOldBreedingCycle = TestData.breedingInitial

            // Set old breeding
            viewModel.setBreeding(actualOldBreedingCycle.id)

            // AI
            val aiDate = LiveDataTestUtil.getValue(viewModel.aiDate)
            assertThat(actualOldBreedingCycle.artificialInsemination.date, isEqualTo(aiDate))

            // Repeat Heat
            val repeatHeatExpectedOn = LiveDataTestUtil.getValue(viewModel.repeatHeatExpectedOn)
            assertThat(
                actualOldBreedingCycle.repeatHeat.expectedOn,
                isEqualTo(repeatHeatExpectedOn)
            )

            val repeatHeatStatus = LiveDataTestUtil.getValue(viewModel.repeatHeatStatus)
            assertThat(actualOldBreedingCycle.repeatHeat.status, isEqualTo(repeatHeatStatus))

            val repeatHeatDoneOn = LiveDataTestUtil.getValue(viewModel.repeatHeatDoneOn)
            assertThat(actualOldBreedingCycle.repeatHeat.doneOn, isEqualTo(repeatHeatDoneOn))

            // Pregnancy check
            val pregnancyCheckExpectedOn =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckExpectedOn)
            assertThat(
                actualOldBreedingCycle.pregnancyCheck.expectedOn,
                isEqualTo(pregnancyCheckExpectedOn)
            )

            val pregnancyCheckStatus = LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatus)
            assertThat(
                actualOldBreedingCycle.pregnancyCheck.status,
                isEqualTo(pregnancyCheckStatus)
            )

            val pregnancyCheckDoneOn = LiveDataTestUtil.getValue(viewModel.pregnancyCheckDoneOn)
            assertThat(
                actualOldBreedingCycle.pregnancyCheck.doneOn,
                isEqualTo(pregnancyCheckDoneOn)
            )

            // Dry off
            val dryOffExpectedOn = LiveDataTestUtil.getValue(viewModel.dryOffExpectedOn)
            assertThat(actualOldBreedingCycle.dryOff.expectedOn, isEqualTo(dryOffExpectedOn))

            val dryOffStatus = LiveDataTestUtil.getValue(viewModel.dryOffStatus)
            assertThat(actualOldBreedingCycle.dryOff.status, isEqualTo(dryOffStatus))

            val dryOffDoneOn = LiveDataTestUtil.getValue(viewModel.dryOffDoneOn)
            assertThat(actualOldBreedingCycle.dryOff.doneOn, isEqualTo(dryOffDoneOn))

            // Calving
            val calvingExpectedOn = LiveDataTestUtil.getValue(viewModel.calvingExpectedOn)
            assertThat(actualOldBreedingCycle.calving.expectedOn, isEqualTo(calvingExpectedOn))

            val calvingStatus = LiveDataTestUtil.getValue(viewModel.calvingStatus)
            assertThat(actualOldBreedingCycle.calving.status, isEqualTo(calvingStatus))

            val calvingDoneOn = LiveDataTestUtil.getValue(viewModel.calvingDoneOn)
            assertThat(actualOldBreedingCycle.calving.doneOn, isEqualTo(calvingDoneOn))
        }

    /**
     * When editing existing breeding back pressed without saving. Show back confirmation.
     */
    @Test
    fun onBackPressedWhileEditingExistingBreeding_showBackPressed() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set breeding cycle
            viewModel.setBreeding(TestData.breedingInitial.id)

            // AiDate gets changed.
            viewModel.aiDate.value = null

            // Call onBackPressed
            viewModel.onBackPressed()

            val showBackConfirmationDialog =
                LiveDataTestUtil.getValue(viewModel.showBackConfirmationDialog)
            assertThat(Unit, isEqualTo(showBackConfirmationDialog?.getContentIfNotHandled()))
        }

    /* Breeding behaviour */

    /**
     *  Initial breeding visible views are
     *      1. Cattle detail Material card                      -|
     *      2. Artificial Insemination title                     |-> Always visible views
     *      3. Artificial Insemination TextInputLayout          -|
     *
     *  Everything else will be hidden
     */
    @Test
    fun initialBreedingView() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Make sure we don't have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertFalse(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertFalse(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertFalse(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertFalse(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertFalse(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertFalse(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertFalse(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertFalse(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertFalse(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertFalse(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When aiDate is set and repeat heat status is none show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *
     *  Everything else will be hidden
     */
    @Test
    fun aiDateIsSet() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertFalse(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertFalse(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertFalse(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When repeat heat status is positive show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *
     *  Everything else will be hidden
     */
    @Test
    fun repeatHeatStatusIsNone() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = null

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertFalse(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertFalse(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertFalse(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When repeat heat status is positive show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Repeat heat date done on
     *
     *  Everything else will be hidden
     */
    @Test
    fun repeatHeatStatusIsPositive() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = true

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertTrue(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertFalse(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertFalse(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertFalse(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When repeat heat status is positive and save called, show breeding completed dialog.
     */
    @Test
    fun saveCalledAndRepeatHeatStatusPositive_showBreedingCompletedDialog() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set cattle
            viewModel.setCattle(TestData.cattle1.id)

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            // Set repeat heat status positive
            viewModel.repeatHeatStatus.value = true

            // Call save
            viewModel.save()

            val showBreedingCompletedDialog =
                LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialog)
            assertThat(Unit, isEqualTo(showBreedingCompletedDialog?.getContentIfNotHandled()))
        }

    /**
     *  When repeat heat status is negative, pregnancy check status is negative
     *  and save called, show breeding completed dialog.
     */
    @Test
    fun saveCalledAndPregnancyCheckStatusNegative_showBreedingCompletedDialog() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set cattle
            viewModel.setCattle(TestData.cattle1.id)

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            // Set repeat heat status positive
            viewModel.repeatHeatStatus.value = false

            // Set pregnancy check status negative
            viewModel.pregnancyCheckStatus.value = false

            // Call save
            viewModel.save()

            val showBreedingCompletedDialog =
                LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialog)
            assertThat(Unit, isEqualTo(showBreedingCompletedDialog?.getContentIfNotHandled()))
        }

    @Test
    fun saveCalledWithCalvingStatusIsTrue_showSaveAndAddNewCattleDialog() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set cattle
            viewModel.setCattle(TestData.cattle1.id)

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            // Set repeat heat status negative
            viewModel.repeatHeatStatus.value = false

            // Pregnancy check status positive
            viewModel.pregnancyCheckStatus.value = true

            // Dry off status positive
            viewModel.dryOffStatus.value = true

            // Calving status positive
            viewModel.calvingStatus.value = true

            // Call save
            viewModel.save()

            // Check we don't show showBreedingCompletedDialog dialog.
            val showBreedingCompletedDialog =
                LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialog)
            assertNull(showBreedingCompletedDialog?.getContentIfNotHandled())

            // Check we show showBreedingCompletedDialogWithAddCattleOption dialog.
            val showBreedingCompletedDialogWithAddCattleOption =
                LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialogWithAddCattleOption)
            assertThat(
                Unit,
                isEqualTo(showBreedingCompletedDialogWithAddCattleOption?.getContentIfNotHandled())
            )
        }

    @Test
    fun saveCalledAndBreedingCompletedConfirmationIsTrue_navigateUpOnSuccess() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set cattle
            viewModel.setCattle(TestData.cattle1.id)

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status positive
            viewModel.repeatHeatStatus.value = true

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            // Call save with breeding confirmation is true
            viewModel.save(breedingCompletedConfirmation = true)

            val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
            assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
        }

    /**
     *  When repeat heat status is negative show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *
     *  Everything else will be hidden
     */
    @Test
    fun repeatHeatStatusIsNegative() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When pregnancy check status is none show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *
     *  Everything else will be hidden
     */
    @Test
    fun pregnancyCheckStatusIsNone() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When pregnancy check status is positive show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *     11. Dry off title
     *     12. Dry off date expected
     *     13. Dry off status
     *
     *  Everything else will be hidden
     */
    @Test
    fun pregnancyCheckStatusIsPositive() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertTrue(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertTrue(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertTrue(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertTrue(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When pregnancy check status is negative show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *
     *  Everything else will be hidden
     */
    @Test
    fun pregnancyCheckStatusIsNegative() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When dry off status is none show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *     11. Dry off title
     *     12. Dry off date expected
     *     13. Dry off status
     *
     *  Everything else will be hidden
     */
    @Test
    fun dryOffStatusIsNone() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status none
        viewModel.dryOffStatus.value = null

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertTrue(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertTrue(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertTrue(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertTrue(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When dry off status is positive show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *     11. Dry off title
     *     12. Dry off date expected
     *     13. Dry off status
     *     14. Dry off date done on
     *     15. Calving title
     *     16. Calving date expected
     *     17. Calving status
     *
     *  Everything else will be hidden
     */
    @Test
    fun dryOffStatusIsPositive() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status none
        viewModel.dryOffStatus.value = true

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertTrue(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertTrue(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertTrue(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertTrue(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertTrue(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertTrue(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertTrue(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertTrue(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  Dry off status is never negative.
     *  In case it's negative.
     *
     *  When dry off status is negative show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *     11. Dry off title
     *     12. Dry off date expected
     *     13. Dry off status
     *
     *  Everything else will be hidden
     */
    @Test
    fun dryOffStatusIsNegative() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status negative
        viewModel.dryOffStatus.value = false

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertTrue(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertTrue(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertTrue(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertTrue(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When calving status is none show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *     11. Dry off title
     *     12. Dry off date expected
     *     13. Dry off status
     *     14. Dry off date done on
     *     15. Calving title
     *     16. Calving date expected
     *     17. Calving status
     *
     *  Everything else will be hidden
     */
    @Test
    fun calvingStatusIsNone() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status none
        viewModel.dryOffStatus.value = true

        // Set calving status none
        viewModel.calvingStatus.value = null

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertTrue(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertTrue(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertTrue(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertTrue(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertTrue(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertTrue(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertTrue(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertTrue(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     *  When calving status is positive show
     *      1. Did by
     *      2. Bull name
     *      3. straw code
     *      4. Repeat heat title
     *      5. Repeat heat date expected
     *      6. Repeat heat status
     *      7. Pregnancy check title
     *      8. Pregnancy check date expected
     *      9. Pregnancy check status
     *     10. Pregnancy check date done on
     *     11. Dry off title
     *     12. Dry off date expected
     *     13. Dry off status
     *     14. Dry off date done on
     *     15. Calving title
     *     16. Calving date expected
     *     17. Calving status
     *     18. Calving status done on
     *
     *  Everything else will be hidden
     */
    @Test
    fun calvingStatusIsPositive() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status positive
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status none
        viewModel.dryOffStatus.value = true

        // Set calving status none
        viewModel.calvingStatus.value = true

        // Make sure we have aiDate.
        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertTrue(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertTrue(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertTrue(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertTrue(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertTrue(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertTrue(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertTrue(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertTrue(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertTrue(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertTrue(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertTrue(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertTrue(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertTrue(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertTrue(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertTrue(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertTrue(calvingDateActualVisibility)
    }

    /**
     *  When flow of UI interaction is like
     *      Sets AI date -> Repeat heat negative -> Pregnancy check positive
     *          -> dry off positive -> Repeat heat none
     */
    @Test
    fun whenAiIsSetRHIsNegativePCIsPositiveDryOffIsPositiveAndRepeatHeatIsNone() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status positive
            viewModel.repeatHeatStatus.value = false

            // Set pregnancy check status positive
            viewModel.pregnancyCheckStatus.value = true

            // Set dry off status none
            viewModel.dryOffStatus.value = true

            // Set repeat heat status again none
            viewModel.repeatHeatStatus.value = null

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            /* AI */

            val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
            assertTrue(didByVisibility)

            val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
            assertTrue(bullNameVisibility)

            val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
            assertTrue(strawCodeVisibility)

            /* Repeat heat */

            val repeatHeatTitleVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
            assertTrue(repeatHeatTitleVisibility)

            val repeatHeatDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
            assertTrue(repeatHeatDateExpectedVisibility)

            val repeatHeatStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
            assertTrue(repeatHeatStatusVisibility)

            val repeatHeatDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
            assertFalse(repeatHeatDateActualVisibility)

            /* Pregnancy check */

            val pregnancyCheckTitleVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
            assertFalse(pregnancyCheckTitleVisibility)

            val pregnancyCheckDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
            assertFalse(pregnancyCheckDateExpectedVisibility)

            val pregnancyCheckStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
            assertFalse(pregnancyCheckStatusVisibility)

            val pregnancyCheckDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
            assertFalse(pregnancyCheckDateActualVisibility)

            /* Dry off */

            val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
            assertFalse(dryOffTitleVisibility)

            val dryOffDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
            assertFalse(dryOffDateExpectedVisibility)

            val dryOffStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
            assertFalse(dryOffStatusVisibility)

            val dryOffDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
            assertFalse(dryOffDateActualVisibility)

            /* Calving */

            val calvingTitleVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
            assertFalse(calvingTitleVisibility)

            val calvingDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
            assertFalse(calvingDateExpectedVisibility)

            val calvingStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
            assertFalse(calvingStatusVisibility)

            val calvingDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
            assertFalse(calvingDateActualVisibility)
        }

    @Test
    fun aiDateIsRemoved_resetEveryThing() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status negative
        viewModel.repeatHeatStatus.value = false

        // Set pregnancy check status positive
        viewModel.pregnancyCheckStatus.value = true

        // Set pregnancy check date done on
        viewModel.pregnancyCheckDoneOn.value = LocalDate.now().plusMonths(3)

        // Make sure we have aiDate.
        val hasAiDateBeforeReset = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDateBeforeReset)

        // Reset aiDate
        viewModel.aiDate.value = null

        // Make sure we don't have aiDate.
        val hasAiDateAfterReset = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertFalse(hasAiDateAfterReset)

        val resetDidBy = LiveDataTestUtil.getValue(viewModel.resetDidBy)!!
        assertTrue(resetDidBy)

        val resetBullName = LiveDataTestUtil.getValue(viewModel.resetBullName)!!
        assertTrue(resetBullName)

        val resetStrawCode = LiveDataTestUtil.getValue(viewModel.resetStrawCode)!!
        assertTrue(resetStrawCode)

        val resetRepeatHeatDateActual =
            LiveDataTestUtil.getValue(viewModel.resetRepeatHeatDateActual)!!
        assertTrue(resetRepeatHeatDateActual)

        val resetPregnancyCheckDateActual =
            LiveDataTestUtil.getValue(viewModel.resetPregnancyCheckDateActual)!!
        assertTrue(resetPregnancyCheckDateActual)

        val resetDryOffDateActual = LiveDataTestUtil.getValue(viewModel.resetDryOffDateActual)!!
        assertTrue(resetDryOffDateActual)

        val resetCalvingDateActual = LiveDataTestUtil.getValue(viewModel.resetCalvingDateActual)!!
        assertTrue(resetCalvingDateActual)

        /* AI */

        val didByVisibility = LiveDataTestUtil.getValue(viewModel.didByVisibility)!!
        assertFalse(didByVisibility)

        val bullNameVisibility = LiveDataTestUtil.getValue(viewModel.bullNameVisibility)!!
        assertFalse(bullNameVisibility)

        val strawCodeVisibility = LiveDataTestUtil.getValue(viewModel.strawCodeVisibility)!!
        assertFalse(strawCodeVisibility)

        /* Repeat heat */

        val repeatHeatTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatTitleVisibility)!!
        assertFalse(repeatHeatTitleVisibility)

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertFalse(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertFalse(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertFalse(repeatHeatDateActualVisibility)

        /* Pregnancy check */

        val pregnancyCheckTitleVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckTitleVisibility)!!
        assertFalse(pregnancyCheckTitleVisibility)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertFalse(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertFalse(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        /* Dry off */

        val dryOffTitleVisibility = LiveDataTestUtil.getValue(viewModel.dryOffTitleVisibility)!!
        assertFalse(dryOffTitleVisibility)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility = LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        /* Calving */

        val calvingTitleVisibility = LiveDataTestUtil.getValue(viewModel.calvingTitleVisibility)!!
        assertFalse(calvingTitleVisibility)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility = LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    @Test
    fun repeatHeatStatusIsNoneFromPositive_resetRepeatHeatDateActualIsTrue() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status as Positive
            viewModel.repeatHeatStatus.value = true

            // Set repeat heat date done on
            viewModel.repeatHeatDoneOn.value = LocalDate.now()

            // Set repeat heat status as None
            viewModel.repeatHeatStatus.value = null

            val resetRepeatHeatDateActual =
                LiveDataTestUtil.getValue(viewModel.resetRepeatHeatDateActual)!!
            assertTrue(resetRepeatHeatDateActual)
        }

    @Test
    fun repeatHeatStatusIsNegativeFromNone_resetRepeatHeatDateActualIsTrue() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status as None
            viewModel.repeatHeatStatus.value = null

            // Set repeat heat date done on
            viewModel.repeatHeatDoneOn.value = LocalDate.now()

            // Set repeat heat status as Negative
            viewModel.repeatHeatStatus.value = false

            val resetRepeatHeatDateActual =
                LiveDataTestUtil.getValue(viewModel.resetRepeatHeatDateActual)!!
            assertTrue(resetRepeatHeatDateActual)
        }

    @Test
    fun pregnancyCheckStatusIsNoneFromPositive_resetPregnancyCheckDateActualIsTrue() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status as Positive
            viewModel.repeatHeatStatus.value = false

            // Set repeat heat date done on
            viewModel.repeatHeatDoneOn.value = LocalDate.now()

            // Set pregnancy check status positive
            viewModel.pregnancyCheckStatus.value = true

            // Set pregnancy check done on
            viewModel.pregnancyCheckDoneOn.value = LocalDate.now()

            // Set pregnancy check status none
            viewModel.pregnancyCheckStatus.value = null

            val resetPregnancyCheckDateActual =
                LiveDataTestUtil.getValue(viewModel.resetPregnancyCheckDateActual)!!
            assertTrue(resetPregnancyCheckDateActual)
        }

    @Test
    fun pregnancyCheckStatusIsNoneFromNegative_resetPregnancyCheckDateActualIsTrue() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status as Positive
            viewModel.repeatHeatStatus.value = false

            // Set repeat heat date done on
            viewModel.repeatHeatDoneOn.value = LocalDate.now()

            // Set pregnancy check status negative
            viewModel.pregnancyCheckStatus.value = false

            // Set pregnancy check done on
            viewModel.pregnancyCheckDoneOn.value = LocalDate.now()

            // Set pregnancy check status none
            viewModel.pregnancyCheckStatus.value = null

            val resetPregnancyCheckDateActual =
                LiveDataTestUtil.getValue(viewModel.resetPregnancyCheckDateActual)!!
            assertTrue(resetPregnancyCheckDateActual)
        }

    @Test
    fun dryOffStatusIsNoneFromPositive_resetDryOffDateActualIsTrue() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status as Positive
            viewModel.repeatHeatStatus.value = false

            // Set repeat heat date done on
            viewModel.repeatHeatDoneOn.value = LocalDate.now()

            // Set pregnancy check status as Positive
            viewModel.pregnancyCheckStatus.value = true

            // Set dry off status as Positive
            viewModel.dryOffStatus.value = true

            // Set dry off date done on
            viewModel.dryOffDoneOn.value = LocalDate.now()

            // Set dry off status as None
            viewModel.dryOffStatus.value = null

            val resetDryOffDateActual =
                LiveDataTestUtil.getValue(viewModel.resetDryOffDateActual)!!
            assertTrue(resetDryOffDateActual)
        }

    @Test
    fun calvingIsNoneFromPositive_resetCalvingDateActualIsTrue() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status as Positive
        viewModel.repeatHeatStatus.value = false

        // Set repeat heat date done on
        viewModel.repeatHeatDoneOn.value = LocalDate.now()

        // Set pregnancy check status as Positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status as Positive
        viewModel.dryOffStatus.value = true

        // Set dry off date done on
        viewModel.dryOffDoneOn.value = LocalDate.now()

        // Set calving status as Positive
        viewModel.calvingStatus.value = true

        // Set calving date done on
        viewModel.calvingDoneOn.value = LocalDate.now()

        // Set calving status as None
        viewModel.calvingStatus.value = null

        val resetCalvingDateActual =
            LiveDataTestUtil.getValue(viewModel.resetCalvingDateActual)!!
        assertTrue(resetCalvingDateActual)
    }

    /**
     * When we reached to calving and repeat heat status gets positive then
     * reset pregnancy check, dry off and calving status data (status, actual date).
     * Visibility of pregnancy check, dry off and calving should be false.
     */
    @Test
    fun reachedToCalvingIsPositive_repeatHeatChangedToPositive() = coroutineRule.runBlockingTest {
        val viewModel = createAddEditBreedingViewModel()

        // Set aiDate
        viewModel.aiDate.value = LocalDate.now()

        // Set repeat heat status as Positive
        viewModel.repeatHeatStatus.value = false

        // Set repeat heat date done on
        viewModel.repeatHeatDoneOn.value = LocalDate.now()

        // Set pregnancy check status as Positive
        viewModel.pregnancyCheckStatus.value = true

        // Set dry off status as Positive
        viewModel.dryOffStatus.value = true

        // Set dry off date done on
        viewModel.dryOffDoneOn.value = LocalDate.now()

        // Set calving status as Positive
        viewModel.calvingStatus.value = true

        // Set calving date done on
        viewModel.calvingDoneOn.value = LocalDate.now()

        // Set repeat heat positive
        viewModel.repeatHeatStatus.value = true

        /**
         * Start checking.
         */

        // Repeat heat
        val repeatHeatStatus = LiveDataTestUtil.getValue(viewModel.repeatHeatStatus)!!
        assertTrue(repeatHeatStatus) // Verify repeat heat status is positive

        val repeatHeatDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
        assertTrue(repeatHeatDateExpectedVisibility)

        val repeatHeatStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
        assertTrue(repeatHeatStatusVisibility)

        val repeatHeatDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
        assertTrue(repeatHeatDateActualVisibility)

        // Pregnancy check
        val pregnancyCheckStatus = LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatus)
        assertNull(pregnancyCheckStatus) // Verify pregnancy check status status is null

        val resetPregnancyCheckDateActual =
            LiveDataTestUtil.getValue(viewModel.resetPregnancyCheckDateActual)!!
        assertTrue(resetPregnancyCheckDateActual)

        val pregnancyCheckDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
        assertFalse(pregnancyCheckDateExpectedVisibility)

        val pregnancyCheckStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
        assertFalse(pregnancyCheckStatusVisibility)

        val pregnancyCheckDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
        assertFalse(pregnancyCheckDateActualVisibility)

        // Dry Off
        val dryOffStatus = LiveDataTestUtil.getValue(viewModel.dryOffStatus)
        assertNull(dryOffStatus) // Verify dry off status status is null

        val resetDryOffDateActual =
            LiveDataTestUtil.getValue(viewModel.resetDryOffDateActual)!!
        assertTrue(resetDryOffDateActual)

        val dryOffDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
        assertFalse(dryOffDateExpectedVisibility)

        val dryOffStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
        assertFalse(dryOffStatusVisibility)

        val dryOffDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
        assertFalse(dryOffDateActualVisibility)

        // Calving
        val calvingStatus = LiveDataTestUtil.getValue(viewModel.calvingStatus)
        assertNull(calvingStatus) // Verify calving status status is null

        val resetCalvingDateActual =
            LiveDataTestUtil.getValue(viewModel.resetCalvingDateActual)!!
        assertTrue(resetCalvingDateActual)

        val calvingDateExpectedVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
        assertFalse(calvingDateExpectedVisibility)

        val calvingStatusVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
        assertFalse(calvingStatusVisibility)

        val calvingDateActualVisibility =
            LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
        assertFalse(calvingDateActualVisibility)
    }

    /**
     * When we reached to calving and pregnancy check status gets negative then
     * reset dry off and calving status data (status, actual date).
     * Visibility of dry off and calving should be false.
     */
    @Test
    fun reachedToCalvingIsPositive_pregnancyCheckChangedToNegative() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Set repeat heat status as Positive
            viewModel.repeatHeatStatus.value = false

            // Set repeat heat date done on
            viewModel.repeatHeatDoneOn.value = LocalDate.now()

            // Set pregnancy check status as Positive
            viewModel.pregnancyCheckStatus.value = true

            // Set dry off status as Positive
            viewModel.dryOffStatus.value = true

            // Set dry off date done on
            viewModel.dryOffDoneOn.value = LocalDate.now()

            // Set calving status as Positive
            viewModel.calvingStatus.value = true

            // Set calving date done on
            viewModel.calvingDoneOn.value = LocalDate.now()

            // Set pregnancy check negative
            viewModel.pregnancyCheckStatus.value = false

            /**
             * Start checking.
             */

            // Repeat heat
            val repeatHeatStatus = LiveDataTestUtil.getValue(viewModel.repeatHeatStatus)!!
            assertFalse(repeatHeatStatus) // Verify repeat heat status is negative

            val repeatHeatDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatDateExpectedVisibility)!!
            assertTrue(repeatHeatDateExpectedVisibility)

            val repeatHeatStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatStatusVisibility)!!
            assertTrue(repeatHeatStatusVisibility)

            val repeatHeatDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.repeatHeatDateActualVisibility)!!
            assertFalse(repeatHeatDateActualVisibility)

            // Pregnancy check
            val pregnancyCheckStatus = LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatus)!!
            assertFalse(pregnancyCheckStatus) // Verify pregnancy check status status is negative

            val resetPregnancyCheckDateActual =
                LiveDataTestUtil.getValue(viewModel.resetPregnancyCheckDateActual)!!
            assertFalse(resetPregnancyCheckDateActual)

            val pregnancyCheckDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateExpectedVisibility)!!
            assertTrue(pregnancyCheckDateExpectedVisibility)

            val pregnancyCheckStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatusVisibility)!!
            assertTrue(pregnancyCheckStatusVisibility)

            val pregnancyCheckDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.pregnancyCheckDateActualVisibility)!!
            assertTrue(pregnancyCheckDateActualVisibility)

            // Dry Off
            val dryOffStatus = LiveDataTestUtil.getValue(viewModel.dryOffStatus)
            assertNull(dryOffStatus) // Verify dry off status status is null

            val resetDryOffDateActual =
                LiveDataTestUtil.getValue(viewModel.resetDryOffDateActual)!!
            assertTrue(resetDryOffDateActual)

            val dryOffDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.dryOffDateExpectedVisibility)!!
            assertFalse(dryOffDateExpectedVisibility)

            val dryOffStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.dryOffStatusVisibility)!!
            assertFalse(dryOffStatusVisibility)

            val dryOffDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.dryOffDateActualVisibility)!!
            assertFalse(dryOffDateActualVisibility)

            // Calving
            val calvingStatus = LiveDataTestUtil.getValue(viewModel.calvingStatus)
            assertNull(calvingStatus) // Verify calving status status is null

            val resetCalvingDateActual =
                LiveDataTestUtil.getValue(viewModel.resetCalvingDateActual)!!
            assertTrue(resetCalvingDateActual)

            val calvingDateExpectedVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingDateExpectedVisibility)!!
            assertFalse(calvingDateExpectedVisibility)

            val calvingStatusVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingStatusVisibility)!!
            assertFalse(calvingStatusVisibility)

            val calvingDateActualVisibility =
                LiveDataTestUtil.getValue(viewModel.calvingDateActualVisibility)!!
            assertFalse(calvingDateActualVisibility)
        }

    @Test
    fun saveCalledForSaveAndAddNewCattle_launchAddNewCattleOnSuccess() =
        coroutineRule.runBlockingTest {
            val viewModel = createAddEditBreedingViewModel()

            // Set cattle
            viewModel.setCattle(TestData.cattle1.id)

            // Set aiDate
            viewModel.aiDate.value = LocalDate.now()

            // Make sure we have aiDate.
            val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
            assertTrue(hasAiDate)

            // Set repeat heat status negative
            viewModel.repeatHeatStatus.value = false

            // Pregnancy check status positive
            viewModel.pregnancyCheckStatus.value = true

            // Dry off status positive
            viewModel.dryOffStatus.value = true

            // Calving status positive
            viewModel.calvingStatus.value = true

            // Call save
            viewModel.save(breedingCompletedConfirmation = true, saveAndAddNewCattle = true)

            val launchAddNewCattle = LiveDataTestUtil.getValue(viewModel.launchAddNewCattleScreen)
            assertThat(TestData.cattle1, isEqualTo(launchAddNewCattle?.getContentIfNotHandled()))
        }
}
