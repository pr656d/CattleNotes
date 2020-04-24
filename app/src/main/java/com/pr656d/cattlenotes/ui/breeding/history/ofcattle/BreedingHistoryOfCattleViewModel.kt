package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryActionListener
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.breeding.addedit.DeleteBreedingUseCase
import com.pr656d.shared.domain.breeding.history.LoadBreedingByCattleIdUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.nameOrTagNumber
import javax.inject.Inject

class BreedingHistoryOfCattleViewModel @Inject constructor(
    private val loadBreedingByCattleIdUseCase: LoadBreedingByCattleIdUseCase,
    private val deleteBreedingUseCase: DeleteBreedingUseCase
) : ViewModel(),
    BreedingHistoryActionListener {

    val cattle = MutableLiveData<Cattle>()

    val nameOrTagNumber = cattle.map { it.nameOrTagNumber() }

    private val deleteBreedingResult = MutableLiveData<Result<Unit>>()

    val breedingList = MediatorLiveData<List<Breeding>>()

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>
        get() = breedingList.map { it.isNullOrEmpty() }

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    private val _launchDeleteConfirmation = MutableLiveData<Event<Breeding>>()
    val launchDeleteConfirmation: LiveData<Event<Breeding>>
        get() = _launchDeleteConfirmation

    private val _launchEditBreeding = MutableLiveData<Event<Pair<Cattle, Breeding>>>()
    val launchEditBreeding: LiveData<Event<Pair<Cattle, Breeding>>>
        get() = _launchEditBreeding

    init {
        _loading.addSource(breedingList) {
            _loading.value = false
        }

        _showMessage.addSource(deleteBreedingResult) {
            (it as? Result.Error)?.exception?.let {
                _showMessage.postValue(Event(R.string.error_unknown))
            }
        }
    }

    fun setCattle(cattle: Cattle) {
        this.cattle.value = cattle

        breedingList.addSource(loadBreedingByCattleIdUseCase(cattle.id)) {
            breedingList.postValue(it)
        }
    }

    override fun editBreeding(breeding: Breeding) {
        _launchEditBreeding.postValue(Event(cattle.value!! to breeding))
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