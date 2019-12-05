package com.pr656d.cattlenotes.shared.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setActivityScreenOrientation()
        setupObservers()
        setupView(savedInstanceState)
    }

    fun showMessage(message: String) = Toaster.show(applicationContext, message)

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    private fun setActivityScreenOrientation(allowLandscape: Boolean = false) {
        /**
         * Default android screen mode allows landscape. To get rid of it and
         * force all activities to be in portrait mode.
         *
         * If landscape needed then override this function and pass true to allow landscape mode.
         */
        if (!allowLandscape) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun setupView(savedInstanceState: Bundle?)

    abstract fun setupObservers()
}