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

package com.pr656d.shared.domain.milk

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.model.Milk
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.fakes.FakePerformanceHelper
import com.pr656d.shared.fakes.milk.FakeMilkRepository
import com.pr656d.test.MainCoroutineRule
import com.pr656d.test.TestData
import com.pr656d.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

@ExperimentalCoroutinesApi
class LoadAllNewMilkFromSmsUseCaseTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private fun createLoadAllNewMilkFromSmsUseCase(
        milkRepository: FakeMilkRepository = FakeMilkRepository()
    ) : LoadAllNewMilkFromSmsUseCase = LoadAllNewMilkFromSmsUseCase(
        milkRepository,
        FakePerformanceHelper(), coroutineRule.testDispatcher
    )

    @Test
    fun bothDataSourceHasNoData_returnEmptyList() = coroutineRule.runBlockingTest {
        val useCase = createLoadAllNewMilkFromSmsUseCase(
            object : FakeMilkRepository() {
                override fun getAllMilk(): Flow<List<Milk>> = flowOf(emptyList())

                override suspend fun getAllMilkFromSms(
                    smsSource: Milk.Source.Sms
                ): List<Milk> = emptyList()
            }
        )

        val result = (useCase(Milk.Source.Sms.BGAMAMCS) as? Result.Success)?.data

        assertThat(0, isEqualTo(result?.size))
    }

    @Test
    fun localDbHasNoDataSmsSourceHasData_returnSmsList() = coroutineRule.runBlockingTest {
        val useCase = createLoadAllNewMilkFromSmsUseCase(
            object : FakeMilkRepository() {
                override fun getAllMilk(): Flow<List<Milk>> = flowOf(emptyList())

                override suspend fun getAllMilkFromSms(
                    smsSource: Milk.Source.Sms
                ): List<Milk> = TestData.milkList
            }
        )

        val result = (useCase(Milk.Source.Sms.BGAMAMCS) as? Result.Success)?.data

        assertThat(TestData.milkList, isEqualTo(result))
    }

    @Test
    fun localDbHasDataAndSmsSourceHasNoData_returnEmptyList() = coroutineRule.runBlockingTest {
        val useCase = createLoadAllNewMilkFromSmsUseCase(
            object : FakeMilkRepository() {
                override fun getAllMilk(): Flow<List<Milk>> = flowOf(TestData.milkList)

                override suspend fun getAllMilkFromSms(
                    smsSource: Milk.Source.Sms
                ): List<Milk> = emptyList()
            }
        )

        val result = (useCase(Milk.Source.Sms.BGAMAMCS) as? Result.Success)?.data

        assertThat(0, isEqualTo(result?.size))
    }

    @Test
    fun localDbHasDataAndSmsSourceHasData_returnNewDataList() = coroutineRule.runBlockingTest {
        val listFromLocalDbSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val listFromSmsSource = listOf(
            TestData.milk3, TestData.milk4
        )

        val useCase = createLoadAllNewMilkFromSmsUseCase(
            object : FakeMilkRepository() {
                override fun getAllMilk(): Flow<List<Milk>> = flowOf(listFromLocalDbSource)

                override suspend fun getAllMilkFromSms(
                    smsSource: Milk.Source.Sms
                ): List<Milk> = listFromSmsSource
            }
        )

        val result = (useCase(Milk.Source.Sms.BGAMAMCS) as? Result.Success)?.data

        assertThat(listFromSmsSource, isEqualTo(result))
    }

    @Test
    fun duplicateDataInSmsSourceNoNewDataInSms_returnEmptyList() = coroutineRule.runBlockingTest {
        val listFromLocalDbSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val listFromSmsSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val useCase = createLoadAllNewMilkFromSmsUseCase(
            object : FakeMilkRepository() {
                override fun getAllMilk(): Flow<List<Milk>> = flowOf(listFromLocalDbSource)

                override suspend fun getAllMilkFromSms(
                    smsSource: Milk.Source.Sms
                ): List<Milk> = listFromSmsSource
            }
        )

        val result = (useCase(Milk.Source.Sms.BGAMAMCS) as? Result.Success)?.data

        assertThat(0, isEqualTo(result?.size))
    }

    @Test
    fun someDataAreDuplicateInSmsSource_returnNewDataList() = coroutineRule.runBlockingTest {
        val listFromLocalDbSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val listFromSmsSource = listOf(
            TestData.milk1, TestData.milk4
        )

        val useCase = createLoadAllNewMilkFromSmsUseCase(
            object : FakeMilkRepository() {
                override fun getAllMilk(): Flow<List<Milk>> = flowOf(listFromLocalDbSource)

                override suspend fun getAllMilkFromSms(
                    smsSource: Milk.Source.Sms
                ): List<Milk> = listFromSmsSource
            }
        )

        val result = (useCase(Milk.Source.Sms.BGAMAMCS) as? Result.Success)?.data

        assertThat(listOf(TestData.milk4), isEqualTo(result))
    }
}