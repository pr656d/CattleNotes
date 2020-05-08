package com.pr656d.cattlenotes.ui.milking

import androidx.lifecycle.*
import com.pr656d.model.Milk
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
    private val loadAllNewMilkFromSmsUseCase: LoadAllNewMilkFromSmsUseCase
) : ViewModel() {

    val milkList: LiveData<List<Milk>> = loadMilkListUseCase()

    val isEmpty: LiveData<Boolean>

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
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

    private val availableMilkSmsSourcesResult = MutableLiveData<Result<List<Milk.Source.Sms>>>()

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
    private val _showMilkFoundMessage = MediatorLiveData<Event<Int>>()
    val showMilkFoundMessage: LiveData<Event<Int>>
        get() = _showMilkFoundMessage

    init {
        getMilkSmsSourceUseCase.executeNow(Unit).let { result ->
            (result as? Result.Success)?.data?.let {
                _smsSource.value = it
            }
        }

        getAvailableMilkSmsSourcesUseCase(Unit, availableMilkSmsSourcesResult)

        _showMilkFoundMessage.addSource(newMilkListFromSms) {
            _showMilkFoundMessage.postValue(Event(it.size))
        }

        _loading.addSource(milkList) {
            _loading.postValue(false)
        }

        isEmpty = milkList.map { it.isNullOrEmpty() }
    }

    fun setPermissionsGranted(isGranted: Boolean) {
        _permissionsGranted.postValue(isGranted)
    }

    fun requestPermission() = _requestPermissions.postValue(Event(Unit))

    fun showPermissionExplanation() = _showPermissionExplanation.postValue(Event(Unit))

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
}