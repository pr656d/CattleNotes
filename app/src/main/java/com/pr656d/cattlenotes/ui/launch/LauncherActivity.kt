/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.launch
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.ui.MainActivity
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.shared.domain.result.EventObserver
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
            val intent = when (destination) {
                LaunchDestination.MainActivity -> Intent(this, MainActivity::class.java)
                LaunchDestination.LoginActivity -> Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })
    }

    override fun onBackPressed() {
        /**
         * While [LauncherActivity] is showing if we press back then it neither go to next screen
         * nor close the app. It stuck on [LauncherActivity].
         */
    }
}