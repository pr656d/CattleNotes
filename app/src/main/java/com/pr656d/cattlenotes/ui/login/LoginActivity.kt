package com.pr656d.cattlenotes.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.viewModelProvider
import com.pr656d.cattlenotes.utils.display.Toaster
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"

        const val CODE_SIGN_IN = 111
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var authUI: AuthUI

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = viewModelProvider(viewModelFactory)

        setupObservers()
        setupView()
    }

    private fun setupObservers() {
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
        })

        viewModel.launchMain.observe(this, EventObserver {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        })

        viewModel.loginStatus.observe(this, Observer {
            it.data?.run { tvMessage.setText(this) }
        })
    }

    private fun setupView() {
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
                }
            }
        }
    }
}