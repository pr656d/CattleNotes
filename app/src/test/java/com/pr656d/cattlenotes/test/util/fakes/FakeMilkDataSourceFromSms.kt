package com.pr656d.cattlenotes.test.util.fakes

import android.telephony.SmsMessage
import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import org.mockito.Mockito

class FakeMilkDataSourceFromSms : MilkDataSourceFromSms {
    override fun getMilk(smsMessage: SmsMessage): Milk {
        return Mockito.mock(MilkDataSourceFromSms::class.java).getMilk(smsMessage)
    }

    override fun getMilk(smsSource: Milk.Source.Sms, messageBody: String): Milk {
        return Mockito.mock(MilkDataSourceFromSms::class.java).getMilk(smsSource, messageBody)
    }

    override fun getAllMilk(smsSource: Milk.Source.Sms): List<Milk> {
        return Mockito.mock(MilkDataSourceFromSms::class.java).getAllMilk(smsSource)
    }
}