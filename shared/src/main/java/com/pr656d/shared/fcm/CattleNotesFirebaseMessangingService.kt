package com.pr656d.shared.fcm

import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/**
 * Receives firebase cloud messages.
 */
class CattleNotesFirebaseMessagingService: DaggerFirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Timber.d("New firebase token $newToken")
        // Nothing to do, we update the user's firebase token via FirebaseAuthStateUserDataSource
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("Firebase message data payload ${remoteMessage.data}")

        if (remoteMessage.data[KEY_TRIGGER_DATA_SYNC] == TRIGGER_DATA_SYNC) {
            // TODO("FCM") : Handle firebase messaging here
        }
    }

    companion object {
        private const val TRIGGER_DATA_SYNC = "SYNC_DATA"
        private const val KEY_TRIGGER_DATA_SYNC = "action_sync_data"
    }
}