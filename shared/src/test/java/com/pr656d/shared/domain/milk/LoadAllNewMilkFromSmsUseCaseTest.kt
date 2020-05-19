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
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.model.Milk
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.test.util.fakes.FakePerformanceHelper
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

class LoadAllNewMilkFromSmsUseCaseTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun bothDataSourceHasNoData_returnEmptyList() {
        val useCase = LoadAllNewMilkFromSmsUseCase(
            mock {
                on { getAllMilkUnobserved() }.doReturn(emptyList<Milk>())
                on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(emptyList<Milk>())
            },
            FakePerformanceHelper()
        )

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(0, isEqualTo(result.size))
    }

    @Test
    fun localDbHasNoDataSmsSourceHasData_returnSmsList() {
        val useCase = LoadAllNewMilkFromSmsUseCase(
            mock {
                on { getAllMilkUnobserved() }.doReturn(emptyList<Milk>())
                on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(TestData.milkList)
            },
            FakePerformanceHelper()
        )

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(TestData.milkList, isEqualTo(result))
    }

    @Test
    fun localDbHasDataAndSmsSourceHasNoData_returnEmptyList() {
        val useCase = LoadAllNewMilkFromSmsUseCase(
            mock {
                on { getAllMilkUnobserved() }.doReturn(TestData.milkList)
                on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(emptyList<Milk>())
            },
            FakePerformanceHelper()
        )

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(0, isEqualTo(result.size))
    }

    @Test
    fun localDbHasDataAndSmsSourceHasData_returnNewDataList() {
        val listFromLocalDbSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val listFromSmsSource = listOf(
            TestData.milk3, TestData.milk4
        )

        val useCase = LoadAllNewMilkFromSmsUseCase(
            mock {
                on { getAllMilkUnobserved() }.doReturn(listFromLocalDbSource)
                on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(listFromSmsSource)
            },
            FakePerformanceHelper()
        )

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(listFromSmsSource, isEqualTo(result))
    }

    @Test
    fun duplicateDataInSmsSourceNoNewDataInSms_returnEmptyList() {
        val listFromLocalDbSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val listFromSmsSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val useCase = LoadAllNewMilkFromSmsUseCase(
            mock {
                on { getAllMilkUnobserved() }.doReturn(listFromLocalDbSource)
                on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(listFromSmsSource)
            },
            FakePerformanceHelper()
        )

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(0, isEqualTo(result.size))
    }

    @Test
    fun someDataAreDuplicateInSmsSource_returnNewDataList() {
        val listFromLocalDbSource = listOf(
            TestData.milk1, TestData.milk2
        )

        val listFromSmsSource = listOf(
            TestData.milk1, TestData.milk4
        )

        val useCase = LoadAllNewMilkFromSmsUseCase(
            mock {
                on { getAllMilkUnobserved() }.doReturn(listFromLocalDbSource)
                on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(listFromSmsSource)
            },
            FakePerformanceHelper()
        )

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(listOf(TestData.milk4), isEqualTo(result))
    }
}