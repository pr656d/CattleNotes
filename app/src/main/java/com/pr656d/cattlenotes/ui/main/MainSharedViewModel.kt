package com.pr656d.cattlenotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class MainSharedViewModel @Inject constructor(): BaseViewModel() {

    private val _refreshCattleListScreen by lazy { MutableLiveData<Unit>() }
    val refreshCattleListScreen: LiveData<Event<Unit>> =
        Transformations.map(_refreshCattleListScreen) { Event(it) }
}