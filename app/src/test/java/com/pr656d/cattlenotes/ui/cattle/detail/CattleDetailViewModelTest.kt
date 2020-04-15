package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetParentCattleUseCase
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [CattleDetailViewModel]
 */
class CattleDetailViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    private val cattleRepository = object : FakeCattleRepository() {
        override fun getCattleById(id: String): LiveData<Cattle?> {
            return MutableLiveData<Cattle?>().apply {
                value = TestData.cattleList.firstOrNull { it.id == id }
            }
        }

        override fun getCattleByTagNumber(tagNumber: Long): LiveData<Cattle?> {
            return MutableLiveData<Cattle?>().apply {
                value = TestData.cattleList.firstOrNull { it.tagNumber == tagNumber }
            }
        }
    }

    private fun createCattleDetailViewModel(
        repository: CattleRepository = cattleRepository,
        getCattleByIdUseCase: GetCattleByIdUseCase = GetCattleByIdUseCase(repository),
        getParentCattleUseCase: GetParentCattleUseCase = GetParentCattleUseCase(repository),
        deleteCattleUseCase: DeleteCattleUseCase = DeleteCattleUseCase(repository)
    ): CattleDetailViewModel {
        return CattleDetailViewModel(
            getCattleByIdUseCase,
            getParentCattleUseCase,
            deleteCattleUseCase
        )
    }

    @Test
    fun fetchCattleIsCalledWithAvailableCattle_setCattle() {
        // Every time test runs we will get any random value from list
        val actualCattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Call the method
        viewModel.fetchCattle(actualCattle)

        val cattle = LiveDataTestUtil.getValue(viewModel.cattle)
        assertThat(actualCattle, isEqualTo(cattle))
    }

    @Test
    fun cattleIsModified_fetchCattleShouldUpdateCattle() {
        // We have cattle already
        val oldCattle = TestData.cattle1

        // Mock the repository
        val cattleRepository = mock<CattleRepository>()

        val viewModel = createCattleDetailViewModel(cattleRepository)

        // Return cattle which will be modified later
        whenever(cattleRepository.getCattleById(oldCattle.id)).thenReturn(MutableLiveData(oldCattle))

        // First call for the method
        viewModel.fetchCattle(oldCattle)

        // Cattle has been modified
        val newCattle = Cattle(
            tagNumber = oldCattle.tagNumber,
            name = "Modified name", // Modified
            image = oldCattle.image,
            type = oldCattle.type,
            breed = Cattle.Breed.JERSEY, // Modified
            group = oldCattle.group,
            lactation = oldCattle.lactation,
            homeBorn = oldCattle.homeBorn,
            purchaseAmount = oldCattle.purchaseAmount,
            purchaseDate = oldCattle.purchaseDate
        ).apply { id = oldCattle.id }

        // Returns new updated cattle
        whenever(cattleRepository.getCattleById(oldCattle.id)).thenReturn(MutableLiveData(newCattle))

        /**
         * Second call for the method
         * Mostly this happens when user modifies and comes back.
         */
        viewModel.fetchCattle(oldCattle)

        val cattle = LiveDataTestUtil.getValue(viewModel.cattle)
        assertThat(newCattle, isEqualTo(cattle))
    }

    @Test
    fun deleteCattleIsCalledWithDeleteConfirmationIsFalse_launchDeleteConfirmation() {
        val viewModel = createCattleDetailViewModel()

        // Delete cattle confirmation called
        viewModel.deleteCattle()

        val launchDeleteConfirmation = LiveDataTestUtil.getValue(viewModel.launchDeleteConfirmation)
        assertThat(Unit, isEqualTo(launchDeleteConfirmation?.getContentIfNotHandled()))
    }

    @Test
    fun deleteCattleIsCalledWithDeleteConfirmationIsTrue_navigateUpOnSuccess() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        // Delete cattle confirmation called
        viewModel.deleteCattle(deleteConfirmation = true)

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }

    @Test
    fun showAllBreedingIsCalled_launchAllBreeding() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        // Call method
        viewModel.showAllBreeding()

        val launchAllBreeding = LiveDataTestUtil.getValue(viewModel.launchAllBreeding)
        assertThat(cattle, isEqualTo(launchAllBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun addNewBreedingIsCalled_launchAddNewBreeding() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        // Call method
        viewModel.addNewBreeding()

        val launchAddBreeding = LiveDataTestUtil.getValue(viewModel.launchAddBreeding)
        assertThat(cattle, isEqualTo(launchAddBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun editCattleIsCalled_launchEditCattle() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        // Call method
        viewModel.editCattle()

        val launchEditCattle = LiveDataTestUtil.getValue(viewModel.launchEditCattle)
        assertThat(cattle, isEqualTo(launchEditCattle?.getContentIfNotHandled()))
    }

    /**
     * Use case that always returns an error when executed.
     */
    object FailingDeleteCattleUseCase : DeleteCattleUseCase(cattleRepository = FakeCattleRepository()) {
        override fun execute(parameters: Cattle) {
            throw Exception("Error!")
        }
    }

    @Test
    fun deleteCattleIsCalled_showMessageOnError() {
        val viewModel = createCattleDetailViewModel(
            deleteCattleUseCase = FailingDeleteCattleUseCase
        )

        val cattle = TestData.cattle1

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        viewModel.deleteCattle(true)

        val showMessage  = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun fetchCattleIsCalled_cattleNotFound_showMessageOnError() {
        val cattle = TestData.cattle1

        val viewModel = createCattleDetailViewModel(
            repository = mock {
                on { getCattleById(cattle.id) }.doReturn(MutableLiveData<Cattle?>(null))
            }
        )

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun cattleIsLoaded_hasParentThenFetchParentDetail() {
        val viewModel = createCattleDetailViewModel()

        val cattle = TestData.cattle2
        val parentCattle = TestData.cattle1 // Parent of cattle2

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        val parentDetail = LiveDataTestUtil.getValue(viewModel.parentCattle)
        assertThat(parentCattle, isEqualTo(parentDetail))
    }
}