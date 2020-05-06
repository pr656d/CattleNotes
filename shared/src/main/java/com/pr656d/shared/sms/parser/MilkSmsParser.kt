package com.pr656d.shared.sms.parser

import android.telephony.SmsMessage
import com.pr656d.model.Milk

/**
 * SMS parser interface for every SMS parser to be implemented.
 */
interface MilkSmsParser {
    /**
     * Convert [SmsMessage.getDisplayMessageBody] into [Milk].
     */
    fun getMilk(message: String): Milk

    /**
     * Holds [SmsMessage.getDisplayOriginatingAddress].
     */
    @Suppress("PROPERTYNAME")
    val SOURCE: Milk.Source
}