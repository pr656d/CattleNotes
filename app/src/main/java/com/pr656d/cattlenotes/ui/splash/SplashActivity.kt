package com.pr656d.cattlenotes.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.ActivityComponent
import com.pr656d.cattlenotes.ui.base.BaseActivity
import com.pr656d.cattlenotes.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : BaseActivity<SplashViewModel>() {

    companion object {
        const val TAG = "SplashActivity"

        const val CODE_SIGN_IN = 111
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

        viewModel.launchFirebaseLoginUI.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivityForResult(
                    authUI.createSignInIntentBuilder()
                        .setAvailableProviders(arrayListOf(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.PhoneBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                            AuthUI.IdpConfig.FacebookBuilder().build(),
                            AuthUI.IdpConfig.AnonymousBuilder().build()
                        ))
                        .setLogo(R.drawable.logo)
                        .setTheme(R.style.FirebaseTheme)
                        .build(),
                    CODE_SIGN_IN
                )
            }
        })

        viewModel.loginStatus.observe(this, Observer {
            when (it) {
                true -> {
                    btnLogin.visibility = View.GONE
                }
                false -> {
                    btnLogin.setText(R.string.try_again_text)
                }
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        viewModel.setFirebaseUser(auth.currentUser)

        btnLogin.setOnClickListener { viewModel.onLoginClick() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CODE_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.onLoginSuccess()
                } else {
                    response?.error?.errorCode?.let { showMessage(it) }
                    viewModel.onLoginFail()
                }
            }
        }
    }
}
