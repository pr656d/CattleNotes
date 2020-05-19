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
import androidx.annotation.WorkerThread
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.settings.GetAutomaticMilkingCollectionUseCase
import com.pr656d.shared.utils.getSmsSourceOrThrow
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.system.measureTimeMillis

/**
 * Handles new SMS arrival.
 */
class SmsBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var milkDataSourceFromSms: MilkDataSourceFromSms

    @Inject lateinit var getPreferredAutomaticMilkingCollectionUseCase: GetAutomaticMilkingCollectionUseCase

    @Inject lateinit var preferredMilkSmsSourceUseCase: GetPreferredMilkSmsSourceUseCase

    @Inject lateinit var addMilkUseCase: AddMilkUseCase

    /**
     * Only run short running tasks.
     * TODO("This execution should be done by WorkManager or JobScheduler")
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Timber.d("Received SMS")

            val countDownLatch = CountDownLatch(1)

            DefaultScheduler.execute {
                val timeTaken = measureTimeMillis {
                    val smsMessages = getMessagesFromIntent(intent)
                    addMilk(smsMessages)
                    countDownLatch.countDown()
                }

                Timber.d("Time taken to add milk at SmsBroadcastReceiver : $timeTaken ms")
            }

            try {
                countDownLatch.await(2000, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                Timber.d(e)
            }
        }
    }

    @WorkerThread
    private fun addMilk(smsMessages: List<SmsMessage>) {
        val isAutomaticMilkingCollectionEnabled =
            getPreferredAutomaticMilkingCollectionUseCase.executeNow(Unit).let {
                (it as? Result.Success)?.data ?: false
            }

        val preferredMilkSmsSource =
            preferredMilkSmsSourceUseCase.executeNow(Unit).let {
                (it as? Result.Success)?.data
            }

        for (smsMessage in smsMessages) {
            // Check if message has message body.
            // If it doesn't continue work.
            smsMessage.displayMessageBody ?: continue

            try {
                // Get milk sms source if possible.
                val smsSource = smsMessage.getSmsSourceOrThrow()

                // Check if AMC feature is disabled.
                if (!isAutomaticMilkingCollectionEnabled) {
                    Timber.d("Milk found but Automatic milking collection feature is disabled.")
                    continue
                }

                // Check if milk sms source matches preferred milk sms source.
                if (smsSource != preferredMilkSmsSource) {
                    Timber.d("Found milk source $smsSource but preferred milk source is $preferredMilkSmsSource.")
                    continue
                }

                // Try to parse message as milk data.
                val milk = milkDataSourceFromSms.getMilk(smsMessage)

                /**
                 *  Message is milk message.
                 */

                // Add milk
                val result = addMilkUseCase.executeNow(milk)

                (result as? Result.Success)?.data.let {
                    Timber.d("Milk added ${milk.id}")
                }

                (result as? Result.Error)?.exception?.let {
                    Timber.d(it, "Failed to add milk : ${it.message}")
                }
            } catch (e: NotAMilkSmsException) {
                Timber.d("Not a milking SMS ${smsMessage.originatingAddress}")
                // Ignore, it's not a milking message.
                continue
            } catch (e: Exception) {
                Timber.e(e, "Could not add milk.")
            }
        }
    }

    private fun getMessagesFromIntent(intent: Intent) =
        Telephony.Sms.Intents.getMessagesFromIntent(intent).toList()
}