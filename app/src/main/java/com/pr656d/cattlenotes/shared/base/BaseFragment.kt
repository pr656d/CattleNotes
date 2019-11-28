package com.pr656d.cattlenotes.shared.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.shared.utils.common.inflate
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<VM : BaseViewModel> : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(provideLayoutId())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObservers()
    }

    fun showMessage(message: String) = context?.let { Toaster.show(it, message) }

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun setupView()

    protected abstract fun setupObservers()

    /**
     * Class implementation required to initialize viewmodel.
     */
    protected abstract fun init()
}
