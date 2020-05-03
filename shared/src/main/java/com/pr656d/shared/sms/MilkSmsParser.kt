package com.pr656d.shared.sms

import android.telephony.SmsMessage
import com.pr656d.model.Milk

/**
 * SMS parser to convert milk SMS into data, if from valid sender.
 *
 * TODO() : Make it remote configurable to easily add more parsers whenever needed without app update.
 */
object MilkSmsParser {

    /**
     * Valid milk SMS sender list.
     * Register new parser here with [SmsParser.SENDER_ID].
     */
    val VALID_SENDERS = listOf(AmulSmsParser.SENDER_ID)

    /**
     * Mediator between [SmsParser]s and caller for simplicity while calling.
     *
     */
    @Throws(Exception::class)
    fun getMilkingData(smsMessage: SmsMessage): Milk {
        val senderId = smsMessage.displayOriginatingAddress
            ?: throw Exception("Originating address not available.")

        /**
         * Throw [NotAMilkSmsException] if not from [VALID_SENDERS].
         */
        if (!VALID_SENDERS.contains(senderId))
            throw NotAMilkSmsException(smsMessage)

        /**
         * Add new branch for new parser.
         */
        return when(senderId) {
            AmulSmsParser.SENDER_ID -> AmulSmsParser.getMilkingData(smsMessage.displayMessageBody)
            // We will not be here, but when needs exhausted else branch.
            else -> throw NotAMilkSmsException(smsMessage)
        }
    }
}

