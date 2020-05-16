package com.pr656d.shared.sms

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.domain.milk.AddMilkUseCase
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

/**
 * Handles new SMS arrival.
 */
class SmsBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var milkDataSourceFromSms: MilkDataSourceFromSms

    @Inject lateinit var addMilkUseCase: AddMilkUseCase

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            Timber.d("Received SMS")

            val messages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            messages.forEach { smsMessage ->
                // Check if we have message body.
                val message = smsMessage.displayMessageBody ?: return

                try {
                    val milk = milkDataSourceFromSms.getMilk(smsMessage)
                    Timber.d("Got milk data : $milk")
                    addMilkUseCase(milk)
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