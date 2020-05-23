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

package com.pr656d.cattlenotes.ui.milking.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.androidtest.util.LiveDataTestUtil
import com.pr656d.cattlenotes.test.fakes.data.milk.FakeMilkRepository
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [AddMilkViewModel].
 */
@ExperimentalCoroutinesApi
class AddMilkViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private fun createAddMilkViewModel(
        milkRepository: MilkRepository = FakeMilkRepository()
    ) : AddMilkViewModel {
        val coroutineDispatcher = coroutineRule.testDispatcher

        return AddMilkViewModel(
            AddMilkUseCase(milkRepository, coroutineDispatcher)
        ).apply { observeUnobserved() }
    }

    private fun AddMilkViewModel.observeUnobserved() {
        dateTime.observeForever {  }
        dateTimeErrorMessage.observeForever {  }
        milkOf.observeForever {  }
        milkOfErrorMessage.observeForever {  }
        quantity.observeForever {  }
        quantityErrorMessage.observeForever {  }
        fat.observeForever {  }
        fatErrorMessage.observeForever {  }
        amount.observeForever {  }
    }

    @Test
    fun saveMilkCalledButAllFieldsEmpty_showMessage() = coroutineRule.runBlockingTest {
        val viewModel = createAddMilkViewModel()

        // Call save milk
        viewModel.saveMilk()

        assertNotNull(LiveDataTestUtil.getValue(viewModel.showMessage))
    }

    @Test
    fun saveCalledWithAllFieldsValid_dismissOnSuccess() = coroutineRule.runBlockingTest {
        val viewModel = createAddMilkViewModel()

        val actualMilk = TestData.milk1

        // Set data.
        viewModel.dateTime.value = actualMilk.timestamp
        viewModel.milkOf.value = actualMilk.milkOf.displayName
        viewModel.quantity.value = actualMilk.quantity.toString()
        viewModel.fat.value = actualMilk.fat.toString()

        // Call save milk
        viewModel.saveMilk()

        val dismiss = LiveDataTestUtil.getValue(viewModel.dismiss)

        assertThat(Unit, isEqualTo(dismiss?.getContentIfNotHandled()))
    }

    @Test
    fun saveCalledWithAllFieldsValid_showMessageOnError() = coroutineRule.runBlockingTest {
        val viewModel = createAddMilkViewModel(
            object : FakeMilkRepository() {
                override suspend fun addMilk(milk: Milk): Long {
                    throw Exception("Error!")
                }
            }
        )

        val actualMilk = TestData.milk1

        // Set data.
        viewModel.dateTime.value = actualMilk.timestamp
        viewModel.milkOf.value = actualMilk.milkOf.displayName
        viewModel.quantity.value = actualMilk.quantity.toString()
        viewModel.fat.value = actualMilk.fat.toString()

        // Call save milk
        viewModel.saveMilk()

        val showMessage = LiveDataTestUtil.getValue(viewModel.showMessage)

        assertNotNull(showMessage?.getContentIfNotHandled())
    }
}