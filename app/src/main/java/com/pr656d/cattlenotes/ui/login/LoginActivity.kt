package com.pr656d.cattlenotes.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ActivityLoginBinding
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.utils.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"

        const val CODE_SIGN_IN = 111
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val model by viewModels<LoginViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_login
        )

        binding.lifecycleOwner = this
        binding.viewModel = model

        model.launchFirebaseAuthUI.observe(this,
            EventObserver {
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                            arrayListOf(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.FacebookBuilder().build(),
                                AuthUI.IdpConfig.AnonymousBuilder().build()
                            )
                        )
                        .setLogo(R.drawable.logo)
                        .setTheme(R.style.FirebaseTheme)
                        .build(),
                    CODE_SIGN_IN
                )
            })

        model.launchMain.observe(this,
            EventObserver {
                startActivity(
                    Intent(this@LoginActivity, MainActivity::class.java)
                )
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CODE_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)
                if (resultCode == Activity.RESULT_OK) {
                    model.onLoginSuccess()
                } else {
                    response?.error?.errorCode?.let {
                        Toaster.showToast(this, it)
                    }
                    model.onLoginFail()
                }
            }
        }
    }
}