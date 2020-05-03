package com.pr656d.shared.sms

import android.telephony.SmsMessage

internal data class NotAMilkSmsException(val smsMessage: SmsMessage) : Exception()