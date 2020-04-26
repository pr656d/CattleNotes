package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeBreedingRepository
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.AddBreedingUseCase
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.test.TestData
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [AddEditBreedingViewModel].
 */
class AddEditBreedingViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private fun getBreedingBehaviour(): BreedingBehaviour =
        BreedingBehaviourImpl(
            BreedingUiImplDelegate(
                GetCattleByIdUseCase(cattleRepository)
            ) as BreedingUiDelegate
        )

    private val cattleRepository = object : FakeCattleRepository() {
        override fun getCattleById(id: String): LiveData<Cattle?> {
            return MutableLiveData<Cattle?>().apply {
                value = TestData.cattleList.firstOrNull { it.id == id }
            }
        }
    }

    private fun createAddEditBreedingViewModel(
        breedingBehaviour: BreedingBehaviour = getBreedingBehaviour(),
        addBreedingUseCase: AddBreedingUseCase = AddBreedingUseCase(FakeBreedingRepository()),
        updateBreedingUseCase: UpdateBreedingUseCase = UpdateBreedingUseCase(FakeBreedingRepository())
    ): AddEditBreedingViewModel {
        return AddEditBreedingViewModel(
            breedingBehaviour = breedingBehaviour,
            addBreedingUseCase = addBreedingUseCase,
            updateBreedingUseCase = updateBreedingUseCase
        ).apply {
            /**
             * For testing we need to initialize them with initial value
             * as they are [MediatorLiveData].
             */
            repeatHeatStatus.value = null
            pregnancyCheckStatus.value = null
            dryOffStatus.value = null
            calvingStatus.value = null
            observeUnobserved()
        }
    }

    private fun AddEditBreedingViewModel.observeUnobserved() {
        cattle.observeForever{}
        repeatHeatExpectedOn.observeForever {}
        pregnancyCheckExpectedOn.observeForever {}
        dryOffExpectedOn.observeForever {}
        calvingExpectedOn.observeForever {}
    }

    @Test
    fun saveCalledButAiDateNotAvailable_hasAiDateFalse() {
        val viewModel = createAddEditBreedingViewModel()

        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertFalse(hasAiDate)

        // Call save
        viewModel.save()
    }

    @Test
    fun saveCalledWithAiDateIsAvailable_hasAiDateTrue() {
        val viewModel = createAddEditBreedingViewModel()

        // Set AI date
        viewModel.aiDate.value = LocalDate.now()

        val hasAiDate = LiveDataTestUtil.getValue(viewModel.hasAiDate)!!
        assertTrue(hasAiDate)

        // Call save
        viewModel.save()
    }

    @Test
    fun saveCalled_navigateUpOnSuccess() {
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
    fun onBackPressedCalledAiDateNotSet_navigateUp() {
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
    fun onBackPressedCalledAiDateIsSet_showBackConfirmation() {
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
    fun onBackPressedCalledAiDateIsSetAndBackConfirmationIsTrue_navigateUp() {
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

    private object FailingAddBreedingUseCase : AddBreedingUseCase(FakeBreedingRepository()) {
        override fun execute(parameters: Breeding) {
            throw Exception("Error!")
        }
    }

    @Test
    fun saveCalled_showMessageOnError() {
        val viewModel = createAddEditBreedingViewModel(
            addBreedingUseCase = FailingAddBreedingUseCase
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
    fun oldBreedingIsAvailable_editingIsTrue() {
        val viewModel = createAddEditBreedingViewModel()

        // Set old breeding
        viewModel.setBreeding(TestData.breedingInitial)

        val editing = LiveDataTestUtil.getValue(viewModel.editing)!!
        assertTrue(editing)
    }

    @Test
    fun editingExistingBreedingCycleBindDataCalled_verifyBindingOfData() {
        val viewModel = createAddEditBreedingViewModel()

        val actualOldBreedingCycle = TestData.breedingInitial

        // Set old breeding
        viewModel.setBreeding(actualOldBreedingCycle)

        // AI
        val aiDate = LiveDataTestUtil.getValue(viewModel.aiDate)
        assertThat(actualOldBreedingCycle.artificialInsemination.date, isEqualTo(aiDate))

        // Repeat Heat
        val repeatHeatExpectedOn = LiveDataTestUtil.getValue(viewModel.repeatHeatExpectedOn)
        assertThat(actualOldBreedingCycle.repeatHeat.expectedOn, isEqualTo(repeatHeatExpectedOn))

        val repeatHeatStatus = LiveDataTestUtil.getValue(viewModel.repeatHeatStatus)
        assertThat(actualOldBreedingCycle.repeatHeat.status, isEqualTo(repeatHeatStatus))

        val repeatHeatDoneOn = LiveDataTestUtil.getValue(viewModel.repeatHeatDoneOn)
        assertThat(actualOldBreedingCycle.repeatHeat.doneOn, isEqualTo(repeatHeatDoneOn))

        // Pregnancy check
        val pregnancyCheckExpectedOn = LiveDataTestUtil.getValue(viewModel.pregnancyCheckExpectedOn)
        assertThat(
            actualOldBreedingCycle.pregnancyCheck.expectedOn,
            isEqualTo(pregnancyCheckExpectedOn)
        )

        val pregnancyCheckStatus = LiveDataTestUtil.getValue(viewModel.pregnancyCheckStatus)
        assertThat(actualOldBreedingCycle.pregnancyCheck.status, isEqualTo(pregnancyCheckStatus))

        val pregnancyCheckDoneOn = LiveDataTestUtil.getValue(viewModel.pregnancyCheckDoneOn)
        assertThat(actualOldBreedingCycle.pregnancyCheck.doneOn, isEqualTo(pregnancyCheckDoneOn))

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
    fun onBackPressedWhileEditingExistingBreeding_showBackPressed() {
        val viewModel = createAddEditBreedingViewModel()

        // Set breeding cycle
        viewModel.setBreeding(TestData.breedingInitial)

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
    fun initialBreedingView() {
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
    fun aiDateIsSet() {
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
    fun repeatHeatStatusIsNone() {
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
    fun repeatHeatStatusIsPositive() {
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
    fun saveCalledAndRepeatHeatStatusPositive_showBreedingCompletedDialog() {
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

        val showBreedingCompletedDialog = LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialog)
        assertThat(Unit, isEqualTo(showBreedingCompletedDialog?.getContentIfNotHandled()))
    }

    /**
     *  When repeat heat status is negative, pregnancy check status is negative
     *  and save called, show breeding completed dialog.
     */
    @Test
    fun saveCalledAndPregnancyCheckStatusNegative_showBreedingCompletedDialog() {
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

        val showBreedingCompletedDialog = LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialog)
        assertThat(Unit, isEqualTo(showBreedingCompletedDialog?.getContentIfNotHandled()))
    }

    /**
     *  When repeat heat status is negative, pregnancy check status is positive, calving status is positive
     *  and save called, show breeding completed dialog.
     */
    @Test
    fun saveCalledAndCalvingStatusPositive_showBreedingCompletedDialog() {
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
        viewModel.pregnancyCheckStatus.value = true

        // Set calving check status positive
        viewModel.calvingStatus.value = true

        // Call save
        viewModel.save()

        val showBreedingCompletedDialog = LiveDataTestUtil.getValue(viewModel.showBreedingCompletedDialog)
        assertThat(Unit, isEqualTo(showBreedingCompletedDialog?.getContentIfNotHandled()))
    }

    @Test
    fun saveCalledAndBreedingCompletedConfirmationIsTrue_navigateUpOnSuccess() {
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
    fun repeatHeatStatusIsNegative() {
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
    fun pregnancyCheckStatusIsNone() {
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
    fun pregnancyCheckStatusIsPositive() {
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
    fun pregnancyCheckStatusIsNegative() {
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
    fun dryOffStatusIsNone() {
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
    fun dryOffStatusIsPositive() {
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
    fun dryOffStatusIsNegative() {
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
    fun calvingStatusIsNone() {
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
    fun calvingStatusIsPositive() {
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
    fun whenAiDateIsSetRepeatHeatIsNegativePregnancyCheckIsPositiveDryOffIsPositiveAndRepeatHeatIsNone() {
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
    fun aiDateIsRemoved_resetEveryThing() {
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
    fun repeatHeatStatusIsNoneFromPositive_resetRepeatHeatDateActualIsTrue() {
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
    fun repeatHeatStatusIsNegativeFromNone_resetRepeatHeatDateActualIsTrue() {
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
    fun pregnancyCheckStatusIsNoneFromPositive_resetPregnancyCheckDateActualIsTrue() {
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
    fun pregnancyCheckStatusIsNoneFromNegative_resetPregnancyCheckDateActualIsTrue() {
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
    fun dryOffStatusIsNoneFromPositive_resetDryOffDateActualIsTrue() {
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
    fun calvingIsNoneFromPositive_resetCalvingDateActualIsTrue() {
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
    fun reachedToCalvingIsPositive_repeatHeatChangedToPositive() {
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
        assertTrue(repeatHeatStatus)    // Verify repeat heat status is positive

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
        assertNull(pregnancyCheckStatus)    // Verify pregnancy check status status is null

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
        assertNull(dryOffStatus)    // Verify dry off status status is null

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
        assertNull(calvingStatus)    // Verify calving status status is null

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
    fun reachedToCalvingIsPositive_pregnancyCheckChangedToNegative() {
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
        assertFalse(repeatHeatStatus)    // Verify repeat heat status is negative

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
        assertFalse(pregnancyCheckStatus)    // Verify pregnancy check status status is negative

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
        assertNull(dryOffStatus)    // Verify dry off status status is null

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
        assertNull(calvingStatus)    // Verify calving status status is null

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
}