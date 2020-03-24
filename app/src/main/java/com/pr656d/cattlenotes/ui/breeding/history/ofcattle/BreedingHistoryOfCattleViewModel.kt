package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.lifecycle.*
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryActionListener
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.DeleteBreedingUseCase
import com.pr656d.shared.domain.breeding.history.LoadBreedingHistoryByCattleIdUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Error
import com.pr656d.shared.domain.result.Result.Success
import com.pr656d.shared.log.Logger
import javax.inject.Inject

class BreedingHistoryOfCattleViewModel @Inject constructor(
    loadBreedingHistoryByCattleIdUseCase: LoadBreedingHistoryByCattleIdUseCase,
    private val deleteBreedingUseCase: DeleteBreedingUseCase
) : ViewModel(),
    BreedingHistoryActionListener {

    val cattle = MutableLiveData<Cattle>()

    private val breedingListResult = MutableLiveData<Result<List<Breeding>>>()
    private val deleteBreedingResult = MutableLiveData<Result<Unit>>()

    val breedingList = MediatorLiveData<List<Breeding>>()

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>
        get() = breedingList.map { it.isNullOrEmpty() }

    private val _showMessage = MediatorLiveData<Event<String>>()
    val showMessage: LiveData<Event<String>>
        get() = _showMessage

    private val _launchDeleteConfirmation = MutableLiveData<Event<Breeding>>()
    val launchDeleteConfirmation: LiveData<Event<Breeding>>
        get() = _launchDeleteConfirmation

    private val _launchEditBreeding = MutableLiveData<Event<Pair<Cattle, Breeding>>>()
    val launchEditBreeding: LiveData<Event<Pair<Cattle, Breeding>>>
        get() = _launchEditBreeding

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

        breedingList.addSource(deleteBreedingResult) {
            (it as? Success)?.let {
                loadBreedingHistoryByCattleIdUseCase(cattle.value!!.id, breedingListResult)
            }
        }
    }

    fun setCattle(cattle: Cattle) {
        this.cattle.value = cattle
    }

    override fun editBreeding(breeding: Breeding) {
        _launchEditBreeding.postValue(
            Event(Pair(cattle.value!!, breeding))
        )
    }

    override fun deleteBreeding(breeding: Breeding, deleteConfirmation: Boolean) {
        if (deleteConfirmation)
            deleteBreedingUseCase(breeding, deleteBreedingResult)
        else
            deleteBreedingConfirmation(breeding)
    }

    private fun deleteBreedingConfirmation(breeding: Breeding) {
        _launchDeleteConfirmation.postValue(Event(breeding))
    }
}