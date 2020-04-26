package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.cattle.addedit.DeleteCattleUseCase
import com.pr656d.shared.domain.cattle.detail.GetCattleByIdUseCase
import com.pr656d.shared.domain.cattle.detail.GetParentCattleUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class CattleDetailViewModel @Inject constructor(
    private val getCattleUseCase: GetCattleByIdUseCase,
    private val getParentCattleUseCase: GetParentCattleUseCase,
    private val deleteCattleUseCase: DeleteCattleUseCase
) : ViewModel() {

    private val deleteCattleResult = MutableLiveData<Result<Unit>>()

    private val cattleId = MutableLiveData<String>()

    val cattle: LiveData<Cattle?> = cattleId.switchMap { getCattleUseCase(it) }

    private val _parentCattle = MediatorLiveData<Cattle?>()
    val parentCattle: LiveData<Cattle?>
        get() = _parentCattle

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val _launchAllBreeding = MutableLiveData<Event<Cattle>>()
    val launchAllBreeding: LiveData<Event<Cattle>>
        get() = _launchAllBreeding

    private val _launchAddBreeding = MutableLiveData<Event<Cattle>>()
    val launchAddBreeding: LiveData<Event<Cattle>>
        get() = _launchAddBreeding

    private val _launchEditCattle = MutableLiveData<Event<Cattle>>()
    val launchEditCattle: LiveData<Event<Cattle>>
        get() = _launchEditCattle

    private val _launchDeleteConfirmation = MutableLiveData<Event<Unit>>()
    val launchDeleteConfirmation: LiveData<Event<Unit>>
        get() = _launchDeleteConfirmation

    private val _navigateUp = MediatorLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    private val _loadingParent = MediatorLiveData<Boolean>().apply { value = true }
    val loadingParent: LiveData<Boolean>
        get() = _loadingParent

    init {
        _showMessage.addSource(cattle) {
            if (it == null) showMessage(R.string.error_cattle_not_found)
        }

        _loading.addSource(cattle) {
            _loading.value = false
        }

        _loading.addSource(deleteCattleResult) {
            _loading.value = false
        }

        _navigateUp.addSource(deleteCattleResult) { result ->
            (result as? Success)?.let {
                navigateUp()
            }
        }

        _showMessage.addSource(deleteCattleResult) { result ->
            (result as? Error)?.let {
                showMessage()
            }
        }

        // Trigger when cattle is available.
        _parentCattle.addSource(cattle) { cattle ->
            // If cattle has parent
            cattle?.parent?.let { parentId ->
                // Fetch parent details as LiveData
                _parentCattle.addSource(getParentCattleUseCase(parentId)) { parentCattle ->
                    _parentCattle.postValue(parentCattle)
                }
            }
        }

        _loadingParent.addSource(parentCattle) {
            _loadingParent.value = false
        }
    }

    fun fetchCattle(id: String) = cattleId.postValue(id)

    fun showAllBreeding() {
        cattle.value?.let {
            _launchAllBreeding.postValue(Event(it))
        }
    }

    fun addNewBreeding() {
        cattle.value?.let {
            _launchAddBreeding.postValue(Event(it))
        }
    }

    fun editCattle() {
        cattle.value?.let {
            _launchEditCattle.postValue(Event(it))
        }
    }

    fun deleteCattle(deleteConfirmation: Boolean = false) {
        if (deleteConfirmation)
            deleteCattleUseCase(cattle.value!!, deleteCattleResult)
        else
            deleteCattleConfirmation()
    }

    private fun deleteCattleConfirmation() {
        _launchDeleteConfirmation.postValue(Event(Unit))
    }

    private fun showMessage(@StringRes messageId: Int = R.string.error_unknown) {
        _showMessage.postValue(Event(messageId))
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }
}