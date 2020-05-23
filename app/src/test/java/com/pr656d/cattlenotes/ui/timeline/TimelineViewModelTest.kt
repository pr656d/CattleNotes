/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.timeline

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.breeding.FakeBreedingRepository
import com.pr656d.model.Breeding
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [TimelineViewModel].
 */
@ExperimentalCoroutinesApi
class TimelineViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private fun createTimelineViewModel(
        breedingRepository: BreedingRepository = object : FakeBreedingRepository() {
            override fun getAllBreedingWithCattle(): Flow<List<BreedingWithCattle>> =
                flowOf(TestData.breedingWithCattleList)
        }
    ): TimelineViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return TimelineViewModel(
            loadTimelineUseCase = LoadTimelineUseCase(breedingRepository, coroutineDispatcher),
            updateBreedingUseCase = UpdateBreedingUseCase(breedingRepository, coroutineDispatcher)
        )
    }

    @Test
    fun validateTimelineList() = coroutineRule.runBlockingTest {
        val viewModel = createTimelineViewModel()

        val breedingWithCattleList = LiveDataTestUtil.getValue(viewModel.timelineList)
        assertThat(TestData.validTimelineList, isEqualTo(breedingWithCattleList))
    }

    @Test
    fun saveBreedingCalled_verifySaved() = coroutineRule.runBlockingTest {
        val newBreeding = TestData.breedingRHNegativePregnancyCheckNone

        val mockBreedingRepository = mock<BreedingRepository> {}

        val viewModel = createTimelineViewModel(mockBreedingRepository)

        val selectedData = TimelineActionListener.ItemTimelineData(
            BreedingWithCattle(
                TestData.breedingWithCattle1.cattle,
                newBreeding
            ),
            false
        )

        // Call on option selected.
        viewModel.saveBreeding(selectedData)

        // Verify update is called.
        verify(mockBreedingRepository, times(1)).updateBreeding(newBreeding)
    }

    @Test
    fun saveBreedingCalled_showMessageOnError() = coroutineRule.runBlockingTest {
        val newBreeding = TestData.breedingRHNegativePregnancyCheckNone

        val viewModel = createTimelineViewModel(
            object : FakeBreedingRepository() {
                override fun getAllBreedingWithCattle(): Flow<List<BreedingWithCattle>> =
                    flowOf(TestData.breedingWithCattleList)

                override suspend fun updateBreeding(breeding: Breeding) {
                    throw Exception("Error!")
                }
            }
        )

        val selectedData = TimelineActionListener.ItemTimelineData(
            BreedingWithCattle(
                TestData.breedingWithCattle1.cattle,
                newBreeding
            ),
            false
        )

        // Save called
        viewModel.saveBreeding(selectedData)

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)
        assertNotNull(showMessage?.getContentIfNotHandled())
    }

    @Test
    fun saveBreedingCalledWithAddNewCattle_launchAddNewCattleOnSuccess() = coroutineRule.runBlockingTest {
        val viewModel = createTimelineViewModel()

        val cattle = TestData.breedingWithCattle1.cattle
        val newBreeding = TestData.breedingRepeatHeatNegative

        val selectedData = TimelineActionListener.ItemTimelineData(
            BreedingWithCattle(
                cattle,
                newBreeding
            ),
            false
        )

        // Save called
        viewModel.saveBreeding(itemTimelineData = selectedData, addNewCattle = true)

        val launchAddNewCattle = LiveDataTestUtil.getValue(viewModel.launchAddNewCattleScreen)
        assertThat(cattle, isEqualTo(launchAddNewCattle?.getContentIfNotHandled()))
    }
}