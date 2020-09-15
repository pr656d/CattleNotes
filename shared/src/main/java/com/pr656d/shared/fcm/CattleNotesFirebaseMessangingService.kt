/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.fcm

import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/**
 * Receives firebase cloud messages.
 */
class CattleNotesFirebaseMessagingService : DaggerFirebaseMessagingService() {

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
