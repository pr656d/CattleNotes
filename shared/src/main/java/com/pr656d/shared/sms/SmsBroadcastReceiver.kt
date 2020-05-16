package com.pr656d.shared.sms

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
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

    @Inject lateinit var preferenceStorageRepository: PreferenceStorageRepository

    private val isAutomaticMilkingCollectionEnabled by lazy {
        preferenceStorageRepository.getAutomaticMilkingCollection()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            Timber.d("Received SMS")

            val smsMessages: Array<SmsMessage> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for (smsMessage in smsMessages) {
                // Check if message has message body.
                // If it doesn't continue work.
                smsMessage.displayMessageBody ?: continue

                try {
                    // Try to parse message as milk data.
                    val milk = milkDataSourceFromSms.getMilk(smsMessage)

                    // Message is milk message.
                    // Check if AMC feature is enabled.
                    if (!isAutomaticMilkingCollectionEnabled) {
                        Timber.d("Milk found but Automatic milking collection feature is disabled.")
                        continue    // Continue work if not.
                    }

                    // Message is milk message and AMC feature enabled.
                    // Add milk
                    addMilkUseCase(milk)

                } catch (e: NotAMilkSmsException) {
                    // Ignore, it's not a milking message.
                    return
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }
}