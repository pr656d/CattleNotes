package com.pr656d.cattlenotes.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity<LoginViewModel>() {

    companion object {
        const val TAG = "LoginActivity"

        const val CODE_SIGN_IN = 111
    }

    @Inject lateinit var authUI: AuthUI

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.activity_login

    override fun setupObservers() {
        viewModel.launchFirebaseAuthUI.observe(this, EventObserver {
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
            btnLogin.isEnabled = false
        })

        viewModel.launchMain.observe(this, EventObserver {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        })

        viewModel.loginStatus.observe(this, Observer {
            it.data?.run { tvMessage.setText(this) }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
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
                    response?.error?.errorCode?.let {
                        Toaster.show(this, getString(it))
                    }
                    viewModel.onLoginFail()
                    btnLogin.isEnabled = true
                }
            }
        }
    }
}