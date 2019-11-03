package com.pr656d.cattlenotes.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.ActivityComponent
import com.pr656d.cattlenotes.ui.base.BaseActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.main.MainActivity
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

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchMain.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(
                    Intent(this@SplashActivity, MainActivity::class.java)
                )
                finish()
            }
        })

        viewModel.launchLogin.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        viewModel.setFirebaseUser(auth.currentUser)
    }
}
