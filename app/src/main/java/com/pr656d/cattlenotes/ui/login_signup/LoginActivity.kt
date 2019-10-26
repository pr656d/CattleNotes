package com.pr656d.cattlenotes.ui.login_signup

import android.os.Bundle
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.ActivityComponent
import com.pr656d.cattlenotes.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity<LoginViewModel>() {

    companion object {
        const val TAG = "LoginActivity"

        const val CODE_SIGN_IN = 1
    }

    @Inject
    lateinit var authUI: AuthUI

    override fun provideLayoutId(): Int = R.layout.activity_login

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchFirebaseLoginUI.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivityForResult(
                    authUI.createSignInIntentBuilder()
                        .setAvailableProviders(arrayListOf(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.FacebookBuilder().build()
                            ))
                        .build(),
                    CODE_SIGN_IN
                )
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        btnLogin.setOnClickListener { viewModel.onLoginClick() }
    }
}