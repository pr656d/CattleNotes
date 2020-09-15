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
package com.pr656d.cattlenotes.ui.cattle.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.cattle.FakeCattleRepository
import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.domain.cattle.list.LoadCattleListUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for the [CattleListViewModel]
 */
@ExperimentalCoroutinesApi
class CattleListViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private fun createCattleListViewModel(
        cattleRepository: CattleRepository = object : FakeCattleRepository() {
            override fun getAllCattle(): Flow<List<Cattle>> = flow {
                emit(TestData.cattleList)
            }
        }
    ): CattleListViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return CattleListViewModel(
            LoadCattleListUseCase(cattleRepository, coroutineDispatcher)
        ).apply { observeUnobserved() }
    }

    private fun CattleListViewModel.observeUnobserved() {
        isEmpty.observeForever { }
    }

    @Test
    fun cattleListIsLoaded() {
        val viewModel = createCattleListViewModel()

        val cattleList = LiveDataTestUtil.getValue(viewModel.cattleList)
        assertThat(cattleList, isEqualTo(TestData.cattleList))
    }

    @Test
    fun openCattleIsCalled_launchCattleDetail() {
        val viewModel = createCattleListViewModel()

        // Take any random cattle from list
        val cattle = TestData.cattleList.random()

        // Call function to open cattle
        viewModel.openCattle(cattle)

        val launchCattleDetail = LiveDataTestUtil.getValue(viewModel.launchCattleDetail)
        assertThat(cattle, isEqualTo(launchCattleDetail?.getContentIfNotHandled()))
    }

    @Test
    fun addCattleIsCalled_launchAddCattle() {
        val viewModel = createCattleListViewModel()

        // Call function to add cattle
        viewModel.addCattle()

        val launchAddCattleScreen = LiveDataTestUtil.getValue(viewModel.launchAddCattleScreen)
        assertThat(Unit, isEqualTo(launchAddCattleScreen?.getContentIfNotHandled()))
    }

    @Test
    fun cattleListIsEmpty_isEmptyShouldBeTrue() {
        val fakeCattleRepository = object : FakeCattleRepository() {
            override fun getAllCattle(): Flow<List<Cattle>> = flowOf(emptyList())
        }

        val viewModel = createCattleListViewModel(fakeCattleRepository)

        val isEmpty = LiveDataTestUtil.getValue(viewModel.isEmpty) ?: true
        assertThat(isEmpty, isEqualTo(true))
    }

    @Test
    fun cattleListIsAvailable_isEmptyShouldBeFalse() {
        val viewModel = createCattleListViewModel()

        val isEmpty = LiveDataTestUtil.getValue(viewModel.isEmpty)!!
        assertFalse(isEmpty)
    }
}
