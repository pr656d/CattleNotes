package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleRepository
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.cattlenotes.shared.domain.cattle.detail.GetCattleUseCase
import com.pr656d.cattlenotes.test.data.TestData
import com.pr656d.cattlenotes.test.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.util.SyncTaskExecutorRule
import com.pr656d.cattlenotes.test.util.fakes.FakeCattleRepository
import org.hamcrest.MatcherAssert.assertThat
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
        val getCattleUseCase = GetCattleUseCase(cattleRepository)
        val deleteCattleUseCase = DeleteCattleUseCase(cattleRepository)

        val viewModel = CattleDetailViewModel(getCattleUseCase, deleteCattleUseCase)

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

        val getCattleUseCase = GetCattleUseCase(cattleRepository)
        val deleteCattleUseCase = DeleteCattleUseCase(cattleRepository)
        val viewModel = CattleDetailViewModel(getCattleUseCase, deleteCattleUseCase)

        `when`(cattleRepository.getCattleById(oldCattle.id)).thenReturn(oldCattle)

        // First call for the method
        viewModel.fetchCattle(oldCattle)

        // Cattle has been modified
        val newCattle = Cattle(
            tagNumber = oldCattle.tagNumber,
            name = "Modified name", // Modified
            image = Cattle.Image(null, null),
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
}