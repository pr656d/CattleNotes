package com.pr656d.cattlenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val navController by lazy { findNavController(R.id.nav_host_main) }

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun setupObservers() {
        viewModel.redirectToLoginScreen.observe(this, EventObserver {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        NavigationUI.setupWithNavController(
            toolbar,
            navController,
            drawer_layout
        )
        NavigationUI.setupWithNavController(navigation_view, navController)
    }

    override fun onBackPressed() {
        drawer_layout.run {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
                return
            } else {
                super.onBackPressed()
            }
        }
    }
}
