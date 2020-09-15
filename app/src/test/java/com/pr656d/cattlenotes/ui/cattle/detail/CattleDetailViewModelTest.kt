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
package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.cattle.FakeCattleRepository
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [CattleDetailViewModel]
 */
@ExperimentalCoroutinesApi
class CattleDetailViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private val cattleRepository = object : FakeCattleRepository() {
        override fun getCattleById(id: String): Flow<Cattle?> = flow {
            emit(TestData.cattleList.firstOrNull { it.id == id })
        }

        override fun getCattleByTagNumber(tagNumber: Long): Flow<Cattle?> = flow {
            emit(TestData.cattleList.firstOrNull { it.tagNumber == tagNumber })
        }
    }

    private fun createCattleDetailViewModel(
        repository: CattleRepository = cattleRepository
    ): CattleDetailViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return CattleDetailViewModel(
            GetCattleByIdUseCase(repository, coroutineDispatcher),
            DeleteCattleUseCase(repository, mock(), coroutineDispatcher)
        ).apply { observeUnobserved() }
    }

    private fun CattleDetailViewModel.observeUnobserved() {
        cattle.observeForever { }
        isCattleTypeBull.observeForever { }
    }

    @Test
    fun fetchCattleIsCalledWithAvailableCattle_setCattle() = coroutineRule.runBlockingTest {
        // Every time test runs we will get any random value from list
        val actualCattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Call the method
        viewModel.fetchCattle(actualCattle.id)

        val cattle = LiveDataTestUtil.getValue(viewModel.cattle)
        assertThat(actualCattle, isEqualTo(cattle))
    }

    @Test
    fun cattleIsModified_fetchCattleShouldUpdateCattle() = coroutineRule.runBlockingTest {
        // We have cattle already
        val oldCattle = TestData.cattle1

        // Mock the repository
        val cattleRepository = mock<CattleRepository>()

        val viewModel = createCattleDetailViewModel(cattleRepository)

        // Return cattle which will be modified later
        whenever(cattleRepository.getCattleById(oldCattle.id)).thenReturn(flowOf(oldCattle))

        // First call for the method
        viewModel.fetchCattle(oldCattle.id)

        // Cattle has been modified
        val newCattle = Cattle(
            id = oldCattle.id,
            tagNumber = oldCattle.tagNumber,
            name = "Modified name", // Modified
            image = oldCattle.image,
            type = oldCattle.type,
            breed = "Jersey", // Modified
            group = oldCattle.group,
            lactation = oldCattle.lactation,
            homeBorn = oldCattle.homeBorn,
            purchaseAmount = oldCattle.purchaseAmount,
            purchaseDate = oldCattle.purchaseDate
        )

        // Returns new updated cattle
        whenever(cattleRepository.getCattleById(oldCattle.id)).thenReturn(flowOf(newCattle))

        /**
         * Second call for the method
         * Mostly this happens when user modifies and comes back.
         */
        viewModel.fetchCattle(oldCattle.id)

        val cattle = LiveDataTestUtil.getValue(viewModel.cattle)
        assertThat(newCattle, isEqualTo(cattle))
    }

    @Test
    fun deleteCattleIsCalledWithDeleteConfirmationIsFalse_launchDeleteConfirmation() =
        coroutineRule.runBlockingTest {
            val viewModel = createCattleDetailViewModel()

            // Delete cattle confirmation called
            viewModel.deleteCattle()

            val launchDeleteConfirmation =
                LiveDataTestUtil.getValue(viewModel.launchDeleteConfirmation)
            assertThat(Unit, isEqualTo(launchDeleteConfirmation?.getContentIfNotHandled()))
        }

    @Test
    fun deleteCattleIsCalledWithDeleteConfirmationIsTrue_navigateUpOnSuccess() =
        coroutineRule.runBlockingTest {
            // Returns any random cattle from the list
            val cattle = TestData.cattleList.random()

            val viewModel = createCattleDetailViewModel()

            // Fetch cattle first
            viewModel.fetchCattle(cattle.id)

            // Delete cattle confirmation called
            viewModel.deleteCattle(deleteConfirmation = true)

            val navigateUp = LiveDataTestUtil.getValue(viewModel.navigateUp)
            assertThat(Unit, isEqualTo(navigateUp?.getContentIfNotHandled()))
        }

    @Test
    fun showAllBreedingIsCalled_launchAllBreeding() = coroutineRule.runBlockingTest {
        val cattle = TestData.cattle1

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        // Call method
        viewModel.showAllBreeding()

        val launchAllBreeding = LiveDataTestUtil.getValue(viewModel.launchAllBreeding)
        assertThat(cattle, isEqualTo(launchAllBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun addNewBreedingIsCalled_launchAddNewBreeding() = coroutineRule.runBlockingTest {
        val cattle = TestData.cattle1

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        // Call method
        viewModel.addNewBreeding()

        val launchAddBreeding = LiveDataTestUtil.getValue(viewModel.launchAddBreeding)
        assertThat(cattle, isEqualTo(launchAddBreeding?.getContentIfNotHandled()))
    }

    @Test
    fun cattleTypeIsBull_doNotLaunchAddBreeding() = coroutineRule.runBlockingTest {
        val cattle = TestData.cattleBull

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        // Call method
        viewModel.addNewBreeding()

        val launchAddBreeding = LiveDataTestUtil.getValue(viewModel.launchAddBreeding)
        assertNull(launchAddBreeding?.getContentIfNotHandled())
    }

    @Test
    fun cattleTypeIsBull_doNotShowAllBreeding() = coroutineRule.runBlockingTest {
        val cattle = TestData.cattleBull

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        // Call method
        viewModel.showAllBreeding()

        val launchAllBreeding = LiveDataTestUtil.getValue(viewModel.launchAllBreeding)
        assertNull(launchAllBreeding?.getContentIfNotHandled())
    }

    @Test
    fun editCattleIsCalled_launchEditCattle() = coroutineRule.runBlockingTest {
        // Returns any random cattle from the list
        val cattle = TestData.cattleList.random()

        val viewModel = createCattleDetailViewModel()

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        // Call method
        viewModel.editCattle()

        val launchEditCattle = LiveDataTestUtil.getValue(viewModel.launchEditCattle)
        assertThat(cattle, isEqualTo(launchEditCattle?.getContentIfNotHandled()))
    }

    @Test
    fun deleteCattleIsCalled_showMessageOnError() = coroutineRule.runBlockingTest {
        val viewModel = createCattleDetailViewModel(
            object : FakeCattleRepository() {
                override fun getCattleById(id: String): Flow<Cattle?> = flow {
                    emit(TestData.cattleList.firstOrNull { it.id == id })
                }

                override fun getCattleByTagNumber(tagNumber: Long): Flow<Cattle?> = flow {
                    emit(TestData.cattleList.firstOrNull { it.tagNumber == tagNumber })
                }

                override suspend fun deleteCattle(cattle: Cattle) {
                    throw Exception("Error!")
                }
            }
        )

        val cattle = TestData.cattle1

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        viewModel.deleteCattle(true)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun fetchCattleIsCalled_cattleNotFound_showMessageOnError() = coroutineRule.runBlockingTest {
        val cattle = TestData.cattle1

        val viewModel = createCattleDetailViewModel(
            repository = mock {
                on { getCattleById(cattle.id) }.doReturn(flowOf(null))
            }
        )

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun cattleIsLoaded_hasParentThenFetchParentDetail() = coroutineRule.runBlockingTest {
        val viewModel = createCattleDetailViewModel()

        val cattle = TestData.cattle2
        val parentCattle = TestData.cattle1 // Parent of cattle2

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        val parentDetail = LiveDataTestUtil.getValue(viewModel.parentCattle)
        assertThat(parentCattle, isEqualTo(parentDetail))
    }

    @Test
    fun cattleHasPrentAndParentHasParent_fetchData() = coroutineRule.runBlockingTest {
        val viewModel = createCattleDetailViewModel()

        val cattle = TestData.cattle5
        val parentCattle = TestData.cattle2 // Parent of cattle5
        val parentParentCattle = TestData.cattle1 // Parent of cattle2

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        val parentDetail = LiveDataTestUtil.getValue(viewModel.parentCattle)
        assertThat(parentCattle, isEqualTo(parentDetail))

        val parentParentDetail = LiveDataTestUtil.getValue(viewModel.parentParentCattle)
        assertThat(parentParentCattle, isEqualTo(parentParentDetail))
    }

    @Test
    fun cattleTypeIsBull_isCattleTypeBullIsTrue() = coroutineRule.runBlockingTest {
        val viewModel = createCattleDetailViewModel()

        val cattle = TestData.cattleBull

        // Fetch cattle first
        viewModel.fetchCattle(cattle.id)

        val isCattleBull = LiveDataTestUtil.getValue(viewModel.isCattleTypeBull)!!
        assertTrue(isCattleBull)
    }
}
