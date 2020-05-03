package com.pr656d.shared.sms

import android.telephony.SmsMessage
import com.pr656d.model.Milk

/**
 * SMS parser interface for every SMS parser to be implemented.
 */
interface SmsParser {
    /**
     * Convert [SmsMessage.getDisplayMessageBody] into [Milk].
     */
    fun getMilkingData(message: String): Milk

    @Suppress("PROPERTYNAME")
    val SENDER_ID: String
}