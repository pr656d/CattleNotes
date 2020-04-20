package com.pr656d.cattlenotes.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.databinding.ItemTimelineBinding
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.Breeding.BreedingEvent.Type
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.domain.result.Event

class TimelineAdapter(
    private val timelineViewModel: TimelineViewModel
) : ListAdapter<BreedingWithCattle, TimelineViewHolder>(BreedingWithCattleDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return TimelineViewHolder(
            ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            timelineViewModel
        )
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TimelineViewHolder(
    private val binding: ItemTimelineBinding,
    private val listener: TimelineActionListener
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var data: BreedingWithCattle

    private val undoObserver by lazy {
        object : Observer<Event<String>> {
            override fun onChanged(event: Event<String>?) {
                event?.peekContent()?.let { id ->
                    if (id != data.breeding.id)
                        return  // Ignore if id doesn't match.
                }

                event?.getContentIfNotHandled()?.let {
                    undoSelection()
                    listener.undoOptionSelected.removeObserver(this)
                }
            }
        }
    }

    init {
        /**
         * Use click listener for each radio button if you want to handle radio group selection
         * for once only. Radio group onCheckedChangedListener called multiple times.
         * https://stackoverflow.com/questions/10263778/radiogroup-calls-oncheckchanged-three-times
         */

        binding.radioButtonNeutral.setOnClickListener {
            binding.executeAfter {
                // Ignore
            }
        }

        binding.radioButtonPositive.setOnClickListener { view ->
            binding.executeAfter {
                val checkedId = view.id
                // Check the card.
                containerMaterialCardView.isChecked = true
                // Disable all radio button except checked button.
                radioGroupBreedingStatus.children.forEach {
                    if (checkedId != it.id) it.isEnabled = false
                }
            }

            listener.onOptionSelected(data, data.newData(false), false)

            // Start waiting for undo
            listener.undoOptionSelected.observeForever(undoObserver)
        }

        binding.radioButtonNegative.setOnClickListener { view ->
            binding.executeAfter {
                val checkedId = view.id
                // Check the card.
                containerMaterialCardView.isChecked = true
                // Disable all radio button except checked button.
                radioGroupBreedingStatus.children.forEach {
                    if (checkedId != it.id) it.isEnabled = false
                }
            }

            listener.onOptionSelected(data, data.newData(true), true)

            // Start waiting for undo
            listener.undoOptionSelected.observeForever(undoObserver)
        }

        /*binding.radioGroupBreedingStatus.setOnCheckedChangeListener { _, checkedId ->
            binding.executeAfter {
                // Default checked button is neutral.
                if (checkedId == radioButtonNeutral.id)
                    return@setOnCheckedChangeListener   // Ignore

                when (checkedId) {
                    radioButtonNegative.id -> {
                        // Check the card.
                        containerMaterialCardView.isChecked = true
                        // Disable all radio button except checked button.
                        radioGroupBreedingStatus.children.forEach {
                            if (checkedId != it.id) it.isEnabled = false
                        }
                        listener.onOptionSelected(data, data.newData(false), false)
                    }

                    radioButtonPositive.id -> {
                        // Check the card.
                        containerMaterialCardView.isChecked = true
                        // Disable all radio button except checked button.
                        radioGroupBreedingStatus.children.forEach {
                            if (checkedId != it.id) it.isEnabled = false
                        }
                        listener.onOptionSelected(data, data.newData(true), true)
                    }
                }

                // Start waiting for undo
                listener.undoOptionSelected.observeForever(undoObserver)
            }
        }*/
    }

    fun bind(data: BreedingWithCattle) {
        this.data = data
        binding.executeAfter {
            cattle = data.cattle
            breedingEvent = data.breeding.nextBreedingEvent
            // Title : Repeat heat ( `cattle name` ) or Repeat heat ( `Tag number` )
            title = "${data.breeding.nextBreedingEvent!!.type.displayName} " +
                    "( ${data.cattle.name ?: data.cattle.tagNumber} )"
        }
    }

    private fun undoSelection() {
        binding.executeAfter {
            // uncheck the card.
            containerMaterialCardView.isChecked = false

            // Enable all radio button.
            radioGroupBreedingStatus.children.forEach {
                it.isEnabled = true
            }

            // Check neutral button.
            radioGroupBreedingStatus.check(radioButtonNeutral.id)
        }
    }


    private fun BreedingWithCattle.newData(newStatus: Boolean?): BreedingWithCattle = breeding.run {
        when (nextBreedingEvent!!.type) {
            Type.REPEAT_HEAT -> BreedingWithCattle(
                cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    BreedingEvent(repeatHeat.expectedOn, newStatus, repeatHeat.doneOn),
                    pregnancyCheck,
                    dryOff,
                    calving
                )
            )
            Type.PREGNANCY_CHECK -> BreedingWithCattle(
                cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    repeatHeat,
                    BreedingEvent(pregnancyCheck.expectedOn, newStatus, pregnancyCheck.doneOn),
                    dryOff,
                    calving
                )
            )
            Type.DRY_OFF -> BreedingWithCattle(
                cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    repeat_heat,
                    pregnancyCheck,
                    BreedingEvent(dryOff.expectedOn, newStatus, dryOff.doneOn),
                    calving
                )
            )
            Type.CALVING -> BreedingWithCattle(
                cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    repeat_heat,
                    pregnancyCheck,
                    dryOff,
                    BreedingEvent(calving.expectedOn, newStatus, calving.doneOn)
                )
            )
            Type.UNKNOWN -> throw IllegalStateException("Breeding type can not be UNKNOWN for breeding event ${this.nextBreedingEvent}")
        }
    }
}

object BreedingWithCattleDiff : DiffUtil.ItemCallback<BreedingWithCattle>() {
    override fun areItemsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem.cattle.id == newItem.cattle.id && oldItem.breeding.id == newItem.breeding.id

    override fun areContentsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem == newItem
}