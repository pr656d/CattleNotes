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

        Timber.d("SmsBroadcastReceiver onReceived()")

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            Timber.d("Received SMS")

            val messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            Timber.d("data : ${messages.forEach { it.displayOriginatingAddress }}")

        }
    }
}