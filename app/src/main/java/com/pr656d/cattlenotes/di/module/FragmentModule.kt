package com.pr656d.cattlenotes.di.module

import androidx.lifecycle.ViewModelProviders
import com.mindorks.bootcamp.instagram.utils.ViewModelProviderFactory
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.ui.base.BaseFragment
import com.pr656d.cattlenotes.ui.cattle.CattleViewModel
import com.pr656d.cattlenotes.ui.cashflow.CashFlowViewModel
import com.pr656d.cattlenotes.ui.milking.MilkingViewModel
import com.pr656d.cattlenotes.ui.timeline.TimelineViewModel
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable


@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @Provides
    fun provideCattleViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): CattleViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(CattleViewModel::class) {
                CattleViewModel(schedulerProvider, compositeDisposable, networkHelper)
            }
        ).get(CattleViewModel::class.java)

    @Provides
    fun provideTimelineViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): TimelineViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(TimelineViewModel::class) {
                TimelineViewModel(schedulerProvider, compositeDisposable, networkHelper)
            }
        ).get(TimelineViewModel::class.java)

    @Provides
    fun provideMilkingViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MilkingViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(MilkingViewModel::class) {
                MilkingViewModel(schedulerProvider, compositeDisposable, networkHelper)
            }
        ).get(MilkingViewModel::class.java)

    @Provides
    fun provideCashFlowViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): CashFlowViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(CashFlowViewModel::class) {
                CashFlowViewModel(schedulerProvider, compositeDisposable, networkHelper)
            }
        ).get(CashFlowViewModel::class.java)
}