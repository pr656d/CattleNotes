package com.pr656d.cattlenotes.ui.milking

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.model.Milk
import com.pr656d.shared.domain.milk.AddAllMilkUseCase
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.LoadAllNewMilkFromSmsUseCase
import com.pr656d.shared.domain.milk.LoadMilkListUseCase
import com.pr656d.shared.domain.milk.sms.GetAvailableMilkSmsSourcesUseCase
import com.pr656d.shared.domain.milk.sms.GetMilkSmsSourceUseCase
import com.pr656d.shared.domain.milk.sms.SetMilkSmsSourceUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class MilkingViewModel @Inject constructor(
    loadMilkListUseCase: LoadMilkListUseCase,
    getAvailableMilkSmsSourcesUseCase: GetAvailableMilkSmsSourcesUseCase,
    getMilkSmsSourceUseCase: GetMilkSmsSourceUseCase,
    private val setMilkSmsSourceUseCase: SetMilkSmsSourceUseCase,
    private val loadAllNewMilkFromSmsUseCase: LoadAllNewMilkFromSmsUseCase,
    private val addMilkUseCase: AddMilkUseCase,
    private val addAllMilkUseCase: AddAllMilkUseCase
) : ViewModel(), MilkingActionListener {

    private val setMilkUseCaseResult = MutableLiveData<Result<Unit>>()
    private val availableMilkSmsSourcesResult = MutableLiveData<Result<List<Milk.Source.Sms>>>()

    val milkList: LiveData<List<Milk>> = loadMilkListUseCase()
    val isEmpty: LiveData<Boolean>

    private val _loading = MediatorLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _permissionsGranted = MutableLiveData<Boolean>(true)
    val permissionsGranted: LiveData<Boolean>
        get() = _permissionsGranted

    private val _requestPermissions = MutableLiveData<Event<Unit>>()
    val requestPermissions: LiveData<Event<Unit>>
        get() = _requestPermissions

    private val _showPermissionExplanation = MutableLiveData<Event<Unit>>()

    val showPermissionExplanation: LiveData<Event<Unit>>
        get() = _showPermissionExplanation

    val availableMilkSmsSources: LiveData<List<Milk.Source.Sms>>
        get() = availableMilkSmsSourcesResult.map {
            (it as? Result.Success)?.data ?: emptyList()
        }
    private val _smsSource = MutableLiveData<Milk.Source.Sms>()

    val smsSource: Milk.Source.Sms?
        get() = _smsSource.value

    private val loadAllNewMilkFromSmsResult = MutableLiveData<Result<List<Milk>>>()

    val newMilkListFromSms: LiveData<List<Milk>>
        get() = loadAllNewMilkFromSmsResult.map {
            (it as? Result.Success)?.data ?: emptyList()
        }
    private val _navigateToSmsSourceSelector = MutableLiveData<Event<Unit>>()

    val navigateToSmsSourceSelector: LiveData<Event<Unit>>
        get() = _navigateToSmsSourceSelector

    private var syncWithSmsMessagesAfterSmsSourceIsSet: Event<Unit>? = null
    // Int is count of data found.
    private val _saveNewMilkDialog = MediatorLiveData<Event<List<Milk>>>()

    val saveNewMilkDialog: LiveData<Event<List<Milk>>>
        get() = _saveNewMilkDialog

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    init {
        getMilkSmsSourceUseCase.executeNow(Unit).let { result ->
            (result as? Result.Success)?.data?.let {
                _smsSource.value = it
            }
        }

        getAvailableMilkSmsSourcesUseCase(Unit, availableMilkSmsSourcesResult)

        _saveNewMilkDialog.addSource(newMilkListFromSms) {
            _saveNewMilkDialog.postValue(Event(it))
        }

        _loading.addSource(milkList) {
            _loading.postValue(false)
        }

        isEmpty = milkList.map { it.isNullOrEmpty() }

        _showMessage.addSource(setMilkUseCaseResult) {
            (it as? Result.Error)?.exception?.let {
                _showMessage.postValue(Event(R.string.error_add_milk))
            }
        }
    }

    fun setPermissionsGranted(isGranted: Boolean) {
        _permissionsGranted.postValue(isGranted)
    }

    fun requestPermission() = _requestPermissions.postValue(Event(Unit))

    fun showPermissionExplanation() = _showPermissionExplanation.postValue(Event(Unit))

    fun addMilk() {
        // TODO("Not yet implemented")
    }

    fun syncWithSmsMessages() {
        if (smsSource == null) {
            // Sms sender not available.
            _navigateToSmsSourceSelector.postValue(Event(Unit))
            /** Set event so that when sms source is set we can start syncing right after that. */
            syncWithSmsMessagesAfterSmsSourceIsSet = Event(Unit)
            return
        }

        // Sms sender available.
        loadAllNewMilkFromSmsUseCase(smsSource!!, loadAllNewMilkFromSmsResult)
    }

    fun setSmsSource(smsSource: Milk.Source.Sms) {
        setMilkSmsSourceUseCase(smsSource)
        _smsSource.value = smsSource
        checkAndSyncWithSmsMessagesAfterSmsSourceIsSet()
    }

    private fun checkAndSyncWithSmsMessagesAfterSmsSourceIsSet() {
        // Check if we have to start syncing for sms messages.
        syncWithSmsMessagesAfterSmsSourceIsSet?.getContentIfNotHandled()?.let {
            syncWithSmsMessages()
            syncWithSmsMessagesAfterSmsSourceIsSet = null  // Reset to null
        }
    }

    fun saveMilk(milkList: List<Milk>) {
        _loading.postValue(true)
        addAllMilkUseCase(milkList, setMilkUseCaseResult)
    }

    fun saveMilk(milk: Milk) {
        _loading.postValue(true)
        addMilkUseCase(milk, setMilkUseCaseResult)
    }

    override fun edit(milk: Milk) {
        // TODO("Not yet implemented")
    }

    override fun delete(milk: Milk) {
        // TODO("Not yet implemented")
    }
}