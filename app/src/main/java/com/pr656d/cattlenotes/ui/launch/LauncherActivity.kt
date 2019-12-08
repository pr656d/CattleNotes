package com.pr656d.cattlenotes.ui.launch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.ui.launch.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.utils.common.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * A 'Trampoline' activity for sending users to an appropriate screen on launch.
 */
class LauncherActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<LaunchViewModel> { viewModelFactory }
        viewModel.launchDestination.observe(this, EventObserver { destination ->
            when (destination) {
                MAIN_ACTIVITY -> startActivity(Intent(this, MainActivity::class.java))
                LOGIN_ACTIVITY -> startActivity(Intent(this, LoginActivity::class.java))
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        })
    }

    override fun onBackPressed() {
        /**
         * While [LauncherActivity] is showing if we press back then it neither go to next screen
         * nor close the app. It stuck on [LauncherActivity].
         */
    }
}