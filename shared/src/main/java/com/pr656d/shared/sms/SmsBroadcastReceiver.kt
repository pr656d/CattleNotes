package com.pr656d.shared.sms

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
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

            val messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            messages.forEach { smsMessage ->
                Timber.d("SENDER : ${smsMessage.displayOriginatingAddress}")
                Timber.d("SMS BODY : ${smsMessage.displayMessageBody}")

                // Check if we have message body.
                val message = smsMessage.displayMessageBody ?: return

                try {
                    val milkingData = MilkSmsParser.getMilkingData(smsMessage)
                    Timber.d("Got milk data : $milkingData")
                } catch (e: NotAMilkSmsException) {
                    // Ignore, it's not a milking message.
                    return
                } catch (e: Exception) {
                    Timber.e(e, "Could not parse message : $message")
                }
            }
        }
    }
}