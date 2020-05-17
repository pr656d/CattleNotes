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

package com.pr656d.shared.sms.parser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.model.Milk
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.test.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [BGAMAMCSSmsParser].
 */
class BGAMAMCSSmsParserTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun getCodeAndCustomerIdTest() {
        val actualDairyCode = 123
        val actualCustomerId = 456

        val str = "Code-($actualDairyCode) $actualCustomerId"

        val (dairyCode, customerId) = BGAMAMCSSmsParser.getDairyCodeAndCustomerId(str)

        assertThat(actualDairyCode, isEqualTo(dairyCode))
        assertThat(actualCustomerId, isEqualTo(customerId))
    }

    @Test
    fun getZonedDateTimeTest() {
        val actualDateTime = TimeUtils.toZonedDateTime(
            16, 1, 2020, 6, 41
        )

        val str = "Date-16/01/2020 06:41"

        assertThat(actualDateTime, isEqualTo(BGAMAMCSSmsParser.getZonedDateTime(str)))
    }

    /*@Test
    fun getShiftTest() {
        val shiftM = Milk.Shift.Morning    // Valid
        val shiftE = Milk.Shift.Evening    // Valid

        val strM = "Shift-M"
        assertThat(shiftM, isEqualTo(BGAMAMCSSmsParser.getShift(strM)))

        val strE = "Shift-E"
        assertThat(shiftE, isEqualTo(BGAMAMCSSmsParser.getShift(strE)))
    }*/

    @Test
    fun getMilkOfTest() {
        val str = "Milk-C"
        assertThat(Milk.MilkOf.Cow, isEqualTo(BGAMAMCSSmsParser.getMilkOf(str)))
    }

    @Test
    fun getQuantityTest() {
        val str = "Qty-45.21"
        assertThat(45.21f, isEqualTo(BGAMAMCSSmsParser.getQuantity(str)))
    }

    @Test
    fun getFatTest() {
        val str = "Fat-3.4"
        assertThat(3.4f, isEqualTo(BGAMAMCSSmsParser.getFat(str)))
    }

    @Test
    fun getAmountTest() {
        val str = "Amt-1057.09"
        assertThat(1057.09f, isEqualTo(BGAMAMCSSmsParser.getAmount(str)))
    }

    @Test
    fun getTotalQuantityTest() {
        val str = "TQty-391.7"
        assertThat(391.7f, isEqualTo(BGAMAMCSSmsParser.getTotalQuantity(str)))
    }

    @Test
    fun getTotalAmountTest() {
        val str = "TAmt-11282.60"
        assertThat(11282.60f, isEqualTo(BGAMAMCSSmsParser.getTotalAmount(str)))
    }

    @Test
    fun getLinkTest() {
        val str = "https://goo.gl/UY1HAC"
        assertThat("https://goo.gl/UY1HAC", isEqualTo(BGAMAMCSSmsParser.getLink(str)))
    }

    @Test
    fun getMilkingDataTest() {
        val data = TestData.milk1
        assertThat(data, isEqualTo(BGAMAMCSSmsParser.getMilk(TestData.milkMessage1)))
    }
}