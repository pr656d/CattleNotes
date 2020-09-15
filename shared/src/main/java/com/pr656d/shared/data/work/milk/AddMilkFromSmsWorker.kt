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
package com.pr656d.shared.data.work.milk

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.domain.invoke
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.failed
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.domain.settings.GetAutomaticMilkingCollectionUseCase
import com.pr656d.shared.performance.PerformanceHelper
import com.pr656d.shared.sms.NotAMilkSmsException
import com.pr656d.shared.utils.getSmsSource
import timber.log.Timber

/**
 * A job that adds milk in database.
 */
class AddMilkFromSmsWorker(
    context: Context,
    private val params: WorkerParameters,
    private val getPreferredMilkSmsSourceUseCase: GetPreferredMilkSmsSourceUseCase,
    private val getAutomaticMilkingCollectionUseCase: GetAutomaticMilkingCollectionUseCase,
    private val milkDataSourceFromSms: MilkDataSourceFromSms,
    private val addMilkUseCase: AddMilkUseCase,
    private val performanceHelper: PerformanceHelper
) : CoroutineWorker(context, params) {

    companion object {
        const val PARAM_MESSAGE_BODY = "param_message_body"
        const val PARAM_ORIGIN_ADDRESS = "param_origin_address"

        private const val TRACE_KEY_ADD_MILK_ON_RECEIVE = "add_milk_from_received_sms"
    }

    override suspend fun doWork(): Result {
        performanceHelper.startTrace(TRACE_KEY_ADD_MILK_ON_RECEIVE)

        getAutomaticMilkingCollectionUseCase().successOr(false).let {
            if (!it) {
                Timber.d("Automatic milking collection feature is disabled.")
                return Result.success()
            }
        }

        val preferredMilkSmsSource = getPreferredMilkSmsSourceUseCase().successOr(null)

        return try {
            val smsSource = params.inputData.getString(PARAM_ORIGIN_ADDRESS).getSmsSource()

            // Check if milk sms source matches preferred milk sms source.
            if (smsSource != preferredMilkSmsSource) {
                Timber.d(
                    """SMS source and preferred SMS source does not match.
                    |Found milk source ${smsSource.ORIGINATING_ADDRESS} but
                    preferred milk source is $preferredMilkSmsSource.""".trimMargin()
                )

                // Do not need to execute further as SMS sources does not match.
                // Indicates this job does not need to be rescheduled.
                return Result.success()
            }

            val messageBody = params.inputData.getString(PARAM_MESSAGE_BODY)
                ?: throw Exception("Message body is empty")

            val milk = milkDataSourceFromSms.getMilk(smsSource, messageBody)

            addMilkUseCase(milk).let {
                if (it.failed) {
                    // Rethrow the exception catch should handle exceptions.
                    throw (it as Error).exception
                }
            }

            // Indicates this job does not need to be rescheduled.
            Result.success()
        } catch (e: NotAMilkSmsException) {
            Timber.d("Not a milking SMS $PARAM_ORIGIN_ADDRESS")
            // Indicates this job does not need to be rescheduled.
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Add milk work failed.")
            // Indicates this job does need to be rescheduled.
            Result.retry()
        } finally {
            performanceHelper.stopTrace(TRACE_KEY_ADD_MILK_ON_RECEIVE)
        }
    }
}
