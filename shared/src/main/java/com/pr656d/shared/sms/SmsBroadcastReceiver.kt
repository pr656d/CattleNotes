package com.pr656d.shared.sms

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.domain.internal.DefaultScheduler
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

/**
 * Handles new SMS arrival.
 */
class SmsBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject lateinit var milkDataSourceFromSms: MilkDataSourceFromSms

    @Inject lateinit var milkRepository: MilkRepository

    @Inject lateinit var preferenceStorageRepository: PreferenceStorageRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        DefaultScheduler.execute {
            if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                Timber.d("Received SMS")

                val isAutomaticMilkingCollectionEnabled by lazy {
                    preferenceStorageRepository.getAutomaticMilkingCollection()
                }

                val preferredMilkSmsSource by lazy {
                    preferenceStorageRepository.getPreferredMilkSmsSource()
                }

                val smsMessages = getMessagesFromIntent(intent)

                for (smsMessage in smsMessages) {
                    // Check if message has message body.
                    // If it doesn't continue work.
                    smsMessage.displayMessageBody ?: continue

                    try {
                        // Try to parse message as milk data.
                        val milk = milkDataSourceFromSms.getMilk(smsMessage)

                        /**
                         *  Message is milk message.
                         */

                         // Check if AMC feature is disabled.
                        if (!isAutomaticMilkingCollectionEnabled) {
                            Timber.d("Milk found but Automatic milking collection feature is disabled.")
                            continue
                        }

                        // Check if milk source matches preferred milk sms source.
                        if (milk.source != preferredMilkSmsSource) {
                            Timber.d("Found milk source ${milk.source} but preferred milk source is $preferredMilkSmsSource.")
                            continue
                        }

                        // Add milk
                        milkRepository.addMilk(milk)

                        Timber.d("Milk added ${milk.id}")
                    } catch (e: NotAMilkSmsException) {
                        // Ignore, it's not a milking message.
                        continue
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    private fun getMessagesFromIntent(intent: Intent) =
        Telephony.Sms.Intents.getMessagesFromIntent(intent).toList()
}