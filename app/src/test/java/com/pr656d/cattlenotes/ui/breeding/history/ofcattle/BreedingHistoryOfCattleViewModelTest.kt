package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeBreedingRepository
import com.pr656d.model.Breeding
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.breeding.addedit.DeleteBreedingUseCase
import com.pr656d.shared.domain.breeding.history.LoadBreedingByCattleIdUseCase
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.test.TestData
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [BreedingHistoryOfCattleViewModel].
 */
class BreedingHistoryOfCattleViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private val cattle = TestData.cattle1
    private val breedingListOfCattle = TestData.breedingList.filter { it.cattleId == cattle.id }

    private val mockCattleRepository = mock<CattleRepository> {
        on { getCattleById(cattle.id) }.doReturn(MutableLiveData(cattle))
    }

    private val mockBreedingRepository: BreedingRepository = mock {
        on { getAllBreedingByCattleId(cattle.id) }.doReturn(MutableLiveData(breedingListOfCattle))
    }

    private val mockBreedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater = mock{}

    private fun createBreedingHistoryOfCattleViewModel(
        loadCattleByCattleIdUseCase: LoadBreedingByCattleIdUseCase =
            LoadBreedingByCattleIdUseCase(mockBreedingRepository),
        deleteBreedingUseCase: DeleteBreedingUseCase =
            DeleteBreedingUseCase(mockBreedingRepository, mockBreedingNotificationAlarmUpdater),
        getCattleByIdUseCase: GetCattleByIdUseCase = GetCattleByIdUseCase(mockCattleRepository)
    ): BreedingHistoryOfCattleViewModel {
        return BreedingHistoryOfCattleViewModel(
            loadBreedingByCattleIdUseCase = loadCattleByCattleIdUseCase,
            deleteBreedingUseCase = deleteBreedingUseCase,
            getCattleByIdUseCase = getCattleByIdUseCase
        ).apply {
            cattle.observeForever {}
        }
    }

    @Test
    fun cattleIsSet_loadBreedingList() {
        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breedingList = LiveDataTestUtil.getValue(viewModel.breedingList)
        assertThat(breedingListOfCattle, isEqualTo(breedingList))
    }

    @Test
    fun editBreedingCalled_launchEditBreeding() {
        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call edit breeding
        viewModel.editBreeding(breeding)

        val launchEditBreeding = LiveDataTestUtil.getValue(viewModel.launchEditBreeding)
        assertThat(cattle to breeding, isEqualTo(launchEditBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun deleteBreedingCalled_launchDeleteBreedingConfirmation() {

        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, false)

        val launchDeleteConfirmation = LiveDataTestUtil.getValue(viewModel.launchDeleteConfirmation)
        assertThat(breeding, isEqualTo(launchDeleteConfirmation?.getContentIfNotHandled()))
    }

    @Test
    fun deleteBreedingCalledWithConfirmation_deleteBreeding() {

        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, true)

        verify(mockBreedingRepository, times(1)).deleteBreeding(breeding)
    }

    @Test
    fun deleteBreedingCalledWithConfirmation_cancelAlarmOfIt() {
        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, true)

        verify(mockBreedingNotificationAlarmUpdater, times(1)).cancelByBreedingId(breeding.id)
    }

    private object FailingDeleteBreedingUseCase : DeleteBreedingUseCase(FakeBreedingRepository(), mock()) {
        override fun execute(parameters: Breeding) {
            throw Exception("Error!")
        }
    }

    @Test
    fun deleteBreedingCalled_showMessageOnError() {
        val viewModel = createBreedingHistoryOfCattleViewModel(
            deleteBreedingUseCase = FailingDeleteBreedingUseCase
        )

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, true)

        val showMessage  = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }
}