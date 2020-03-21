package com.pr656d.cattlenotes.ui.breeding.history

import androidx.lifecycle.*
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.history.LoadBreedingHistoryByCattleIdUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.log.Logger
import javax.inject.Inject

class BreedingHistoryViewModel @Inject constructor(
    loadBreedingHistoryByCattleIdUseCase: LoadBreedingHistoryByCattleIdUseCase
) : ViewModel() {

    val cattle = MutableLiveData<Cattle>()

    private val breedingListResult = MutableLiveData<Result<List<Breeding>>>()

    val breedingList = MediatorLiveData<List<Breeding>>()

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>
        get() = breedingList.map { it.isNullOrEmpty() }

    private val _showMessage = MediatorLiveData<Event<String>>()
    val showMessage: LiveData<Event<String>>
        get() = _showMessage

    init {
        breedingList.addSource(cattle) {
            loadBreedingHistoryByCattleIdUseCase(it.id, breedingListResult)
        }

        breedingList.addSource(breedingListResult) { result ->
            (result as? Success)?.let {
                breedingList.value = it.data
            }
        }

        _showMessage.addSource(breedingListResult) { result ->
            (result as? Error)?.exception?.let {
                _showMessage.value = Event(it.localizedMessage)
                Logger.e("BreedingListResult", "${it.printStackTrace()}")
            }
        }

        _loading.addSource(breedingListResult) {
            _loading.value = false
        }
    }

    fun setCattle(cattle: Cattle) {
        this.cattle.value = cattle
    }
}