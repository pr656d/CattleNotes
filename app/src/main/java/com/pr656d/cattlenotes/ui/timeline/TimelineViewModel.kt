package com.pr656d.cattlenotes.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.timeline.LoadTimelineUseCase
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    loadTimelineUseCase: LoadTimelineUseCase
) : ViewModel() {

    private val breedingListResult = loadTimelineUseCase.observe()

    private val _timelineList = MediatorLiveData<List<BreedingWithCattle>>()
    val timelineList: LiveData<List<BreedingWithCattle>> = _timelineList

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>

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
    }
}