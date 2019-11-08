package com.pr656d.cattlenotes.ui.cattle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.utils.rx.RxSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CattleViewModel @Inject constructor(
    networkHelper: NetworkHelper,
    private val compositeDisposable: CompositeDisposable,
    private val schedulerProvider: RxSchedulerProvider,
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel(networkHelper) {

    private val _cattleList = MutableLiveData<List<Cattle>>()
    val cattleList: LiveData<List<Cattle>> = _cattleList

    override fun onCreate() {
        compositeDisposable.add(
            cattleDataRepository.loadSampleData()
                .subscribeOn(schedulerProvider.io())
                .subscribe {
                    cattleDataRepository.getAllCattle()
                        .subscribeOn(schedulerProvider.io())
                        .subscribe { list, _ ->
                            _cattleList.postValue(list)
                        }
                }
        )
    }

    override fun onCleared() {
        compositeDisposable.clear() // Clear manually
        super.onCleared()
    }
}