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
package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.breeding.FakeBreedingRepository
import com.pr656d.cattlenotes.test.fakes.data.cattle.FakeCattleRepository
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.breeding.addedit.DeleteBreedingUseCase
import com.pr656d.shared.domain.breeding.history.LoadBreedingByCattleIdUseCase
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [BreedingHistoryOfCattleViewModel].
 */
@ExperimentalCoroutinesApi
class BreedingHistoryOfCattleViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private val cattle = TestData.cattle1

    private val breedingListOfCattle = TestData.breedingList.filter { it.cattleId == cattle.id }

    private val fakeCattleRepository = object : FakeCattleRepository() {
        override fun getCattleById(id: String): Flow<Cattle?> = flowOf(cattle)
    }

    private val fakeBreedingRepository = object : FakeBreedingRepository() {
        override fun getAllBreedingByCattleId(cattleId: String): Flow<List<Breeding>> =
            flowOf(breedingListOfCattle)
    }

    private val mockBreedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater = mock {}

    private fun createBreedingHistoryOfCattleViewModel(
        cattleRepository: CattleRepository = fakeCattleRepository,
        breedingRepository: BreedingRepository = fakeBreedingRepository
    ): BreedingHistoryOfCattleViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return BreedingHistoryOfCattleViewModel(
            LoadBreedingByCattleIdUseCase(breedingRepository, coroutineDispatcher),
            DeleteBreedingUseCase(
                breedingRepository,
                mockBreedingNotificationAlarmUpdater,
                coroutineDispatcher
            ),
            GetCattleByIdUseCase(cattleRepository, coroutineDispatcher)
        ).apply {
            cattle.observeForever { }
        }
    }

    @Test
    fun cattleIsSet_loadBreedingList() = coroutineRule.runBlockingTest {
        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breedingList = LiveDataTestUtil.getValue(viewModel.breedingList)
        assertThat(breedingListOfCattle, isEqualTo(breedingList))
    }

    @Test
    fun editBreedingCalled_launchEditBreeding() = coroutineRule.runBlockingTest {
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
    fun deleteBreedingCalled_launchDeleteBreedingConfirmation() = coroutineRule.runBlockingTest {
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
    fun deleteBreedingCalledWithConfirmation_deleteBreeding() = coroutineRule.runBlockingTest {
        val mockBreedingRepository = mock<BreedingRepository> { }

        val viewModel = createBreedingHistoryOfCattleViewModel(
            breedingRepository = mockBreedingRepository
        )

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, true)

        verify(mockBreedingRepository, times(1)).deleteBreeding(breeding)
    }

    @Test
    fun deleteBreedingCalledWithConfirmation_cancelAlarmOfIt() = coroutineRule.runBlockingTest {
        val viewModel = createBreedingHistoryOfCattleViewModel()

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, true)

        verify(mockBreedingNotificationAlarmUpdater, times(1)).cancelByBreedingId(breeding.id)
    }

    @Test
    fun deleteBreedingCalled_showMessageOnError() = coroutineRule.runBlockingTest {
        val viewModel = createBreedingHistoryOfCattleViewModel(
            breedingRepository = object : FakeBreedingRepository() {
                override suspend fun deleteBreeding(breeding: Breeding) {
                    throw Exception("Error!")
                }
            }
        )

        // Set cattle
        viewModel.setCattle(cattle.id)

        val breeding = breedingListOfCattle.first()

        // Call delete breeding
        viewModel.deleteBreeding(breeding, true)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }
}
