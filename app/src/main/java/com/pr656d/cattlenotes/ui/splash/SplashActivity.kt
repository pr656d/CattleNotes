package com.pr656d.cattlenotes.ui.splash

import android.os.Bundle
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.ActivityComponent
import com.pr656d.cattlenotes.ui.base.BaseActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.ui.splash.SplashViewModel.LaunchDestination
import com.pr656d.cattlenotes.utils.common.startActivity
import javax.inject.Inject

class SplashActivity : BaseActivity<SplashViewModel>() {

    companion object {
        const val TAG = "SplashActivity"
    }

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var authUI: AuthUI

    override fun provideLayoutId(): Int = R.layout.activity_splash

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setup() {
        viewModel.setFirebaseUser(auth.currentUser)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchDestination.observe(this, Observer {
            it.getIfNotHandled()?.run {
                when (this) {
                    LaunchDestination.LOGIN_ACTIVITY -> startActivity(LoginActivity::class.java)
                    LaunchDestination.MAIN_ACTIVITY -> startActivity(MainActivity::class.java)
                }
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) { }
}
