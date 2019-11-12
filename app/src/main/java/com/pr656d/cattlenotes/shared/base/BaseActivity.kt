package com.pr656d.cattlenotes.shared.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel> : DaggerAppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setActivityScreenOrientation()
        init()
        setupObservers()
        setupView(savedInstanceState)
        viewModel.onCreate()
    }

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })

        viewModel.messageStringId.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
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

    /**
     * Class implementation required to initialize viewmodel.
     */
    protected abstract fun init()
}