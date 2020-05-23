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

package com.pr656d.cattlenotes.test.fakes.data.milk

import android.telephony.SmsMessage
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import org.mockito.Mockito

class FakeMilkDataSourceFromSms : MilkDataSourceFromSms {
    override suspend fun getMilk(smsMessage: SmsMessage): Milk {
        return Mockito.mock(MilkDataSourceFromSms::class.java).getMilk(smsMessage)
    }

    override suspend fun getMilk(smsSource: Milk.Source.Sms, messageBody: String): Milk {
        return Mockito.mock(MilkDataSourceFromSms::class.java).getMilk(smsSource, messageBody)
    }

    override suspend fun getAllMilk(smsSource: Milk.Source.Sms): List<Milk> {
        return Mockito.mock(MilkDataSourceFromSms::class.java).getAllMilk(smsSource)
    }
}