package com.pr656d.cattlenotes.ui.timeline

import androidx.lifecycle.*
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

    private val _timelineList = MediatorLiveData<List<BreedingWithCattle>>()
    val timelineList: LiveData<List<BreedingWithCattle>> = _timelineList

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    val isEmpty: LiveData<Boolean>

    private val _undoOptionSelected = MutableLiveData<Event<String>>()
    override val undoOptionSelected: LiveData<Event<String>> = _undoOptionSelected

    // Event with Breeding id and message
    private val _showUndo = MutableLiveData<Event<Pair<String, String>>>()
    val showUndo: LiveData<Event<Pair<String, String>>> = _showUndo

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

    fun undoOptionSelected(breedingId: String) {
        _undoOptionSelected.postValue(Event(breedingId))
    }

    override fun onOptionSelected(
        oldBreedingWithCattle: BreedingWithCattle,
        newBreedingWithCattle: BreedingWithCattle,
        selectedOption: Boolean?
    ) {
        val message = oldBreedingWithCattle.undoMessage(selectedOption)
        _showUndo.postValue(Event(Pair(oldBreedingWithCattle.breeding.id, message)))
    }

    /**
     * Return message like "Repeat heat marked as positive for `name` or `tagNumber`"
     */
    private fun BreedingWithCattle.undoMessage(actionTaken: Boolean?): String {
        val typeDisplayName = breeding.nextBreedingEvent!!.type.displayName
        val actionName = actionTaken?.let { if (it) "positive" else "negative" } ?: "none"
        val nameOrTagNumber = cattle.name?.let { it } ?: cattle.tagNumber.toString()

        return "$typeDisplayName marked as $actionName of $nameOrTagNumber"
    }
}
