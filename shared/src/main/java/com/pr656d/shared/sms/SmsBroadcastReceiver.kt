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

package com.pr656d.shared.sms

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pr656d.shared.data.work.milk.AddMilkFromSmsWorker
import com.pr656d.shared.data.work.milk.AddMilkFromSmsWorker.Companion.PARAM_MESSAGE_BODY
import com.pr656d.shared.data.work.milk.AddMilkFromSmsWorker.Companion.PARAM_ORIGIN_ADDRESS
import com.pr656d.shared.utils.getSmsSourceOrThrow
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber

/**
 * Handles new SMS arrival.
 */
class SmsBroadcastReceiver : DaggerBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Timber.d("Received SMS")

            for (smsMessage in getMessagesFromIntent(intent)) {
                if (!smsMessage.isSmsSource())
                    continue

                // In case not available ignore.
                val messageBody = smsMessage.displayMessageBody ?: continue
                // In case not available ignore.
                val smsSource = smsMessage.originatingAddress ?: continue

                val data = Data.Builder().apply {
                    putString(PARAM_ORIGIN_ADDRESS, smsSource)
                    putString(PARAM_MESSAGE_BODY, messageBody)
                }.build()

                val workRequest = OneTimeWorkRequestBuilder<AddMilkFromSmsWorker>()
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)

                Timber.d("Worker scheduled to add milk.")
            }
        }
    }

    /**
     * Check if [SmsMessage] is our milk SMS message.
     */
    private fun SmsMessage.isSmsSource(): Boolean = try {
        getSmsSourceOrThrow()
        true
    } catch (e: NotAMilkSmsException) {
        false
    }

    private fun getMessagesFromIntent(intent: Intent) =
        Telephony.Sms.Intents.getMessagesFromIntent(intent).toList()
}