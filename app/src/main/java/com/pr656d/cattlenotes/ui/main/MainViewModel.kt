package com.pr656d.cattlenotes.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : BaseViewModel() {

    private val _activeMenuItem = MutableLiveData<ActiveMenuItem>(
        ActiveMenuItem(
            currentItem = ActiveMenuItem.Item(
                R.id.cattleListScreen,
                R.drawable.ic_cattle_selected,
                R.drawable.ic_cattle_unselected
            )
        )
    )
    val activeMenuItem: LiveData<ActiveMenuItem> = _activeMenuItem

    fun setActiveMenuItem(@IdRes menuItemId: Int) {
        when (menuItemId) {
            R.id.cattleListScreen -> ActiveMenuItem(
                previousItem = _activeMenuItem.value!!.currentItem,
                currentItem = ActiveMenuItem.Item(
                    menuItemId, R.drawable.ic_cattle_selected, R.drawable.ic_cattle_unselected
                )
            )
            R.id.timelineScreen -> ActiveMenuItem(
                previousItem = _activeMenuItem.value!!.currentItem,
                currentItem = ActiveMenuItem.Item(
                    menuItemId, R.drawable.ic_timeline_selected, R.drawable.ic_timeline_unselected
                )
            )
            R.id.milkingScreen -> ActiveMenuItem(
                previousItem = _activeMenuItem.value!!.currentItem,
                currentItem = ActiveMenuItem.Item(
                    menuItemId, R.drawable.ic_milking_selected, R.drawable.ic_milking_unselected
                )
            )
            R.id.cashflowScreen -> ActiveMenuItem(
                previousItem = _activeMenuItem.value!!.currentItem,
                currentItem = ActiveMenuItem.Item(
                    menuItemId, R.drawable.ic_cashflow_selected, R.drawable.ic_cashflow_unselected
                )
            )
            else -> null
        }?.also {
            _activeMenuItem.postValue(it)
        }
    }

    data class ActiveMenuItem(var previousItem: Item? = null, val currentItem: Item) {
        data class Item(
            @IdRes val itemId: Int,
            @DrawableRes val selectedIcon: Int,
            @DrawableRes val unselectedIcon: Int
        )
    }
}