package com.pr656d.cattlenotes.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.ActivityComponent
import com.pr656d.cattlenotes.ui.base.BaseActivity
import com.pr656d.cattlenotes.ui.main.MainActivity

class SplashActivity : BaseActivity<SplashViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_splash

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchMain.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }
        })

        viewModel.launchLogin.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {}
}
