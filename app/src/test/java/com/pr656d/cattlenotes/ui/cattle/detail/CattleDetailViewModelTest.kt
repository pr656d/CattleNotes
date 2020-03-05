package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.cattlenotes.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.cattlenotes.test.data.TestData
import com.pr656d.cattlenotes.test.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
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

    @Test
    fun fetchCattleIsCalledWithAvailableCattle_setCattle() {
        // Every time test runs we will get any random value from list
        val actualCattle = TestData.cattleList.random()

        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                return actualCattle
            }
        }
        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

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

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

        `when`(cattleRepository.getCattleById(oldCattle.id)).thenReturn(oldCattle)

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

        `when`(cattleRepository.getCattleById(oldCattle.id)).thenReturn(newCattle)

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
        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                // Returns any random cattle from the list
                return TestData.cattleList.random()
            }
        }

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

        // Delete cattle confirmation called
        viewModel.deleteCattle()

        val launchDeleteConfirmation = LiveDataTestUtil.getValue(viewModel.launchDeleteConfirmation)
        assertThat(Unit, isEqualTo(launchDeleteConfirmation?.getContentIfNotHandled()))
    }

    @Test
    fun deleteCattleIsCalledWithDeleteConfirmationIsTrue_navigateUpOnSuccess() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                return cattle
            }
        }

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

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

        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                return cattle
            }
        }

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

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

        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                return cattle
            }
        }

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        // Call method
        viewModel.addNewBreeding()

        val launchAddBreeding = LiveDataTestUtil.getValue(viewModel.launchAddBreeding)
        assertThat(cattle, isEqualTo(launchAddBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun showActiveBreedingIsCalled_launchActiveBreeding() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                return cattle
            }
        }

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        // Call method
        viewModel.showActiveBreeding()

        val launchActiveBreeding = LiveDataTestUtil.getValue(viewModel.launchActiveBreeding)
        assertThat(cattle, isEqualTo(launchActiveBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun editCattleIsCalled_launchEditCattle() {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val cattleRepository = object : FakeCattleRepository() {
            override fun getCattleById(id: Long): Cattle {
                return cattle
            }
        }

        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(cattleRepository),
            DeleteCattleUseCase(cattleRepository)
        )

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
        val viewModel = CattleDetailViewModel(
            GetCattleByIdUseCase(FakeCattleRepository()),
            FailingDeleteCattleUseCase
        )

        val cattle = TestData.cattle1

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        viewModel.deleteCattle(true)

        val showMessage  = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertTrue(showMessage?.getContentIfNotHandled() != null)
    }

    /**
     * Use case that always returns an error when executed.
     */
    object FailingGetCattleUseCase : GetCattleByIdUseCase(cattleRepository = FakeCattleRepository()) {
        override fun execute(parameters: Long): Cattle {
            throw Exception("Error!")
        }
    }

    @Test
    fun fetchCattleIsCalled_showMessageOnError() {
        val viewModel = CattleDetailViewModel(
            FailingGetCattleUseCase,
            DeleteCattleUseCase(FakeCattleRepository())
        )

        val cattle = TestData.cattle1

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertTrue(showMessage?.getContentIfNotHandled() != null)
    }

    @Test
    fun fetchCattleIsCalled_navigateUpOnError() {
        val viewModel = CattleDetailViewModel(
            FailingGetCattleUseCase,
            DeleteCattleUseCase(FakeCattleRepository())
        )

        val cattle = TestData.cattle1

        // Fetch cattle first
        viewModel.fetchCattle(cattle)

        val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
        assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
    }
}