package com.pr656d.shared.utils

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkHelper {

    /**
     * Returns network status.
     */
    fun isNetworkConnected(): Boolean

}

@Singleton
class NetworkHelperImpl @Inject constructor(private val context: Context): NetworkHelper {
    override fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }
}