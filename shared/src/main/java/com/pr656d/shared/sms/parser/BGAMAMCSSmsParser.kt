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
package com.pr656d.shared.sms.parser

import com.pr656d.model.Milk
import com.pr656d.model.Milk.Source.Sms.BGAMAMCS
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.toMilkOf
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.ZonedDateTime

/**
 * Sms parser for BGAMAMCS sender AKA Amul to convert it into [Milk] data.
 *
 *  BGAMAMCS sample message :
 *      Code-(264) 1047
 *      Date-16/01/2020 06:41
 *      Shift-M
 *      Milk-C
 *      Qty-36.5
 *      Fat-3.8
 *      Amt-1057.09
 *      TQty-391.7
 *      TAmt-11282.60
 *      https://goo.gl/UY1HAC
 */
object BGAMAMCSSmsParser : MilkSmsParser {

    override val SOURCE = BGAMAMCS

    override suspend fun getMilk(message: String): Milk = coroutineScope {
        // Divide message by lines.
        val parts = message.lines()

        // val (dairyCode, customerId) = async { getDairyCodeAndCustomerId(parts[0]) }
        val timeStamp = async { getZonedDateTime(parts[1]) }
        // val shift = async { getShift(parts[2]) }
        val milkOf = async { getMilkOf(parts[3]) }
        val qty = async { getQuantity(parts[4]) }
        val fat = async { getFat(parts[5]) }
        val amt = async { getAmount(parts[6]) }
        val tQty = async { getTotalQuantity(parts[7]) }
        val tAmt = async { getTotalAmount(parts[8]) }
        val link = async { getLink(parts[9]) }

        Milk(
            SOURCE, timeStamp.await(), milkOf.await(), qty.await(), fat.await(),
            amt.await(), tQty.await(), tAmt.await(), link.await()
        ).apply {
            // This milk is new, Assign new id.
            id = FirestoreUtil.autoId()
        }
    }

    /**
     * Parses `Code-(DAIRY_CODE) CUSTOMER_ID` and returns pair of dairyCode and customerId.
     * Make it open for testing.
     */
    fun getDairyCodeAndCustomerId(str: String): Pair<Int, Int> {
        return CODE_REGEX.find(str)!!
            .destructured
            .let { (dairyCode, customerId) ->
                dairyCode.toInt() to customerId.toInt()
            }
    }

    /**
     * Parses `Date-dd/mm/yyyy hour:minute` and returns epoch millis of it.
     * Make it open for testing.
     */
    fun getZonedDateTime(str: String): ZonedDateTime {
        return DATE_TIME_REGEX.find(str)!!
            .destructured
            .let { (dd, mm, yyyy, hour, minute) ->
                TimeUtils.toZonedDateTime(
                    dd.toInt(),
                    mm.toInt(),
                    yyyy.toInt(),
                    hour.toInt(),
                    minute.toInt()
                )
            }
    }

    /**
     * Parses `Shift-M or E` returns char.
     * Make it open for testing.
     */
    /*fun getShift(str: String): Milk.Shift {
        return SHIFT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.first().toMilkShift() }
    }*/

    /**
     * Parses `Milk-C or B` returns char.
     * Make it open for testing.
     */
    fun getMilkOf(str: String): Milk.MilkOf {
        return MILK_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.first().toMilkOf() }
    }

    /**
     * Parses `Qty-123.12` returns float value.
     * Make it open for testing.
     */
    fun getQuantity(str: String): Float {
        return QTY_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `Fat-3.4` returns float value.
     * Make it open for testing.
     */
    fun getFat(str: String): Float {
        return FAT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `Amt-1250.23` returns float.
     * Make it open for testing.
     */
    fun getAmount(str: String): Float {
        return AMT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `TQty-42.32` returns float.
     * Make it open for testing.
     */
    fun getTotalQuantity(str: String): Float {
        return TQTY_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `TAmt-2311.12` returns float.
     * Make it open for testing.
     */
    fun getTotalAmount(str: String): Float {
        return TAMT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses
     * Make it open for testing.
     */
    fun getLink(str: String): String {
        return LINK_REGEX.find(str)!!
            .destructured
            .let { (value) -> value }
    }

    private val CODE_REGEX = "Code-\\((\\d+)\\) (\\d+)".toRegex()
    private val DATE_TIME_REGEX =
        "Date-(\\d{1,2})/(\\d{1,2})/(\\d{4}) (\\d{1,2}):(\\d{1,2})".toRegex()
    private val SHIFT_REGEX = "Shift-([ME])".toRegex()
    private val MILK_REGEX = "Milk-(\\S)".toRegex()
    private val QTY_REGEX = "Qty-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val FAT_REGEX = "Fat-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val AMT_REGEX = "Amt-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val TQTY_REGEX = "TQty-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val TAMT_REGEX = "TAmt-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val LINK_REGEX = "(\\S+)".toRegex()
}
