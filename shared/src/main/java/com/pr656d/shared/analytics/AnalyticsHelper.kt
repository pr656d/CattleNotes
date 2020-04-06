package com.pr656d.shared.analytics

import android.app.Activity

/** Analytics API */
interface AnalyticsHelper {
    /** Record screen view */
    fun setScreenView(screenName: String, activity: Activity)

    /** Record a UI event, e.g. user clicks a button */
    fun logUiEvent(itemId: String, action: String)
}