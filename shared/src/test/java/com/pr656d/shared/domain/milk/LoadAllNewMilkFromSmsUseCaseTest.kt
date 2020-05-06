package com.pr656d.shared.domain.milk

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.pr656d.model.Milk
import com.pr656d.shared.domain.result.Result
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
        val useCase = LoadAllNewMilkFromSmsUseCase(mock {
            on { getAllMilkUnobserved() }.doReturn(emptyList<Milk>())
            on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(emptyList<Milk>())
        })

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(0, isEqualTo(result.size))
    }

    @Test
    fun localDbHasNoDataSmsSourceHasData_returnSmsList() {
        val useCase = LoadAllNewMilkFromSmsUseCase(mock {
            on { getAllMilkUnobserved() }.doReturn(emptyList<Milk>())
            on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(TestData.milkList)
        })

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(TestData.milkList, isEqualTo(result))
    }

    @Test
    fun localDbHasDataAndSmsSourceHasNoData_returnEmptyList() {
        val useCase = LoadAllNewMilkFromSmsUseCase(mock {
            on { getAllMilkUnobserved() }.doReturn(TestData.milkList)
            on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(emptyList<Milk>())
        })

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

        val useCase = LoadAllNewMilkFromSmsUseCase(mock {
            on { getAllMilkUnobserved() }.doReturn(listFromLocalDbSource)
            on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(listFromSmsSource)
        })

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

        val useCase = LoadAllNewMilkFromSmsUseCase(mock {
            on { getAllMilkUnobserved() }.doReturn(listFromLocalDbSource)
            on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(listFromSmsSource)
        })

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

        val useCase = LoadAllNewMilkFromSmsUseCase(mock {
            on { getAllMilkUnobserved() }.doReturn(listFromLocalDbSource)
            on { getAllMilkFromSms(Milk.Source.Sms.BGAMAMCS) }.doReturn(listFromSmsSource)
        })

        val result = (useCase.executeNow(Milk.Source.Sms.BGAMAMCS) as Result.Success).data

        assertThat(listOf(TestData.milk4), isEqualTo(result))
    }
}