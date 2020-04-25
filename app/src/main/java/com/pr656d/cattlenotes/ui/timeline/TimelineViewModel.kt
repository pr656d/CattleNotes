package com.pr656d.cattlenotes.ui.timeline

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.timeline.TimelineActionListener.ItemTimelineSaveData
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.domain.breeding.addedit.UpdateBreedingUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    loadTimelineUseCase: LoadTimelineUseCase,
    private val updateBreedingUseCase: UpdateBreedingUseCase
) : ViewModel(), TimelineActionListener {

    private val breedingListResult = loadTimelineUseCase.observe()

    private val updateBreedingResult = MutableLiveData<Result<Unit>>()

    private val _timelineList = MediatorLiveData<List<BreedingWithCattle>>()
    val timelineList: LiveData<List<BreedingWithCattle>> = _timelineList

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage = _showMessage

    init {
        loadTimelineUseCase.execute(Unit)

        _timelineList.addSource(breedingListResult) { result ->
            (result as? Result.Success)?.data?.let {
                _timelineList.postValue(it)
            }
        }

        isEmpty = timelineList.map { it.isNullOrEmpty() }

        _loading.addSource(timelineList) {
            _loading.postValue(false)
        }

        _showMessage.addSource(updateBreedingResult) {
            (it as? Result.Error)?.exception?.let { e ->
                _showMessage.postValue(Event(R.string.error_unknown))
            }
        }
    }

    override fun saveBreeding(itemTimelineSaveData: ItemTimelineSaveData) {
        val newBreeding = itemTimelineSaveData.newBreedingWithCattle.breeding
        updateBreedingUseCase(newBreeding, updateBreedingResult)
    }
}
