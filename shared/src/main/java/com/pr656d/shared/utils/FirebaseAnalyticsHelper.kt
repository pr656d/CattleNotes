package com.pr656d.shared.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.pr656d.shared.analytics.AnalyticsHelper
import timber.log.Timber

class FirebaseAnalyticsHelper(
    context: Context
) : AnalyticsHelper {

    private var analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    init {
        analytics.setAnalyticsCollectionEnabled(true)

        Timber.d("Analytics initialized")
    }

    override fun setScreenView(screenName: String, activity: Activity) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, screenName)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_SCREENVIEW)
        }
        analytics.run {
            setCurrentScreen(activity, screenName, null)
            logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
            Timber.d("Screen View recorded: $screenName")
        }
    }

    override fun logUiEvent(itemId: String, action: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, FA_CONTENT_TYPE_UI_EVENT)
            putString(FA_KEY_UI_ACTION, action)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
        Timber.d("Event recorded for $itemId, $action")
    }

    companion object {
        /**
         * Log a specific screen view under the `screenName` string.
         */
        private const val FA_CONTENT_TYPE_SCREENVIEW = "screen"
        private const val FA_KEY_UI_ACTION = "ui_action"
        private const val FA_CONTENT_TYPE_UI_EVENT = "ui_event"
    }
}