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
package com.pr656d.shared.data.milk.datasource

import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract.Channels.CONTENT_URI
import android.provider.CalendarContract.Calendars.DEFAULT_SORT_ORDER
import android.provider.Telephony.TextBasedSmsColumns.ADDRESS
import android.provider.Telephony.TextBasedSmsColumns.BODY
import android.provider.Telephony.TextBasedSmsColumns.DATE
import android.provider.Telephony.TextBasedSmsColumns.TYPE
import android.telephony.SmsMessage
import com.pr656d.model.Milk
import com.pr656d.shared.sms.parser.BGAMAMCSSmsParser
import com.pr656d.shared.utils.FirestoreUtil
import com.pr656d.shared.utils.getDisplayMessageBodyOrThrow
import com.pr656d.shared.utils.getSmsSourceOrThrow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

interface MilkDataSourceFromSms {
    /**
     * Return [Milk] from [SmsMessage].
     */
    suspend fun getMilk(smsMessage: SmsMessage): Milk

    /**
     * Return [Milk] by parsing [messageBody] for [smsSource].
     */
    suspend fun getMilk(smsSource: Milk.Source.Sms, messageBody: String): Milk

    /**
     * This function reads all SMS of [Milk.Source.Sms] and parses each message into [Milk] and
     * @return list of Milk.
     */
    suspend fun getAllMilk(smsSource: Milk.Source.Sms): List<Milk>
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
    override suspend fun getMilk(smsMessage: SmsMessage): Milk {
        val milkSmsSender = smsMessage.getSmsSourceOrThrow()
        val messageBody = smsMessage.getDisplayMessageBodyOrThrow()

        return getMilk(milkSmsSender, messageBody)
    }

    /**
     * Return [Milk] by parsing [messageBody] for [smsSource].
     */
    override suspend fun getMilk(smsSource: Milk.Source.Sms, messageBody: String): Milk {
        /**  Add new branch for new parser.  */
        return when (smsSource) {
            Milk.Source.Sms.BGAMAMCS -> BGAMAMCSSmsParser.getMilk(messageBody)
        }.apply {
            // This milk is new, Assign new id.
            id = FirestoreUtil.autoId()
        }
    }

    /**
     * This function reads all SMS of [Milk.Source.Sms] and parses each message into [Milk] and
     * @return list of Milk.
     */
    @ExperimentalCoroutinesApi
    override suspend fun getAllMilk(smsSource: Milk.Source.Sms): List<Milk> = coroutineScope {
        createSmsCursor(smsSource)
            ?.use {
                produce {
                    while (it.moveToNext()) {
                        val milk = getMilk(smsSource, it.getString(it.getColumnIndex(BODY)))
                        send(milk)
                    }
                }.toList()
            } ?: emptyList()
    }

    /**
     * Create cursor for [smsSource].
     */
    private fun createSmsCursor(smsSource: Milk.Source.Sms): Cursor? {
        val contentResolver = context.contentResolver ?: return null

        val projection = listOf(
            ADDRESS,
            BODY,
            DATE,
            TYPE
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
