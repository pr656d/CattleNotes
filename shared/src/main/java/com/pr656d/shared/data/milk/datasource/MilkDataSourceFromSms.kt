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

package com.pr656d.shared.data.milk.datasource

import android.content.Context
import android.database.Cursor
import android.provider.Telephony.Sms.*
import android.telephony.SmsMessage
import com.pr656d.model.Milk
import com.pr656d.shared.sms.parser.BGAMAMCSSmsParser
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.getDisplayMessageBodyOrThrow
import com.pr656d.shared.utils.getSmsSourceOrThrow
import javax.inject.Inject

interface MilkDataSourceFromSms {
    /**
     * Return [Milk] from [SmsMessage].
     */
    fun getMilk(smsMessage: SmsMessage): Milk

    /**
     * Return [Milk] by parsing [messageBody] for [smsSource].
     */
    fun getMilk(smsSource: Milk.Source.Sms, messageBody: String): Milk

    /**
     * This function reads all SMS of [Milk.Source.Sms] and parses each message into [Milk] and
     * @return list of Milk.
     */
    fun getAllMilk(smsSource: Milk.Source.Sms): List<Milk>
}

/**
 * Responsible for handling request to get [Milk] related data from SMS.
 */
class MilkDataSourceFromSmsImpl @Inject constructor(
    private val context: Context
) : MilkDataSourceFromSms {

    /**
     * Return [Milk] from [SmsMessage].
     */
    override fun getMilk(smsMessage: SmsMessage): Milk {
        val milkSmsSender = smsMessage.getSmsSourceOrThrow()
        val messageBody = smsMessage.getDisplayMessageBodyOrThrow()

        return getMilk(milkSmsSender, messageBody).apply {
            // This milk is new, Assign new id.
            id = FirestoreUtil.autoId()
        }
    }

    /**
     * Return [Milk] by parsing [messageBody] for [smsSource].
     */
    override fun getMilk(smsSource: Milk.Source.Sms, messageBody: String): Milk {
        /**
         * Add new branch for new parser.
         */
        return when(smsSource) {
            Milk.Source.Sms.BGAMAMCS -> BGAMAMCSSmsParser.getMilk(messageBody)
        }
    }

    /**
     * This function reads all SMS of [Milk.Source.Sms] and parses each message into [Milk] and
     * @return list of Milk.
     */
    override fun getAllMilk(smsSource: Milk.Source.Sms): List<Milk> {
        val cursor = createSmsCursor(smsSource) ?: return emptyList()

        val list = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { getMilk(smsSource, it.getString(it.getColumnIndex(BODY))) }
            .toList()

        cursor.close()

        return list
    }

    /**
     * Create cursor for [smsSource].
     */
    private fun createSmsCursor(smsSource: Milk.Source.Sms): Cursor? {
        val contentResolver = context.contentResolver ?: return null

        val projection = listOf(
            ADDRESS, BODY, DATE, TYPE
        ).toTypedArray()

        return contentResolver.query(
            CONTENT_URI,
            projection,
            "$ADDRESS='${smsSource.SENDER_ADDRESS}'",
            null,
            DEFAULT_SORT_ORDER
        )
    }
}

