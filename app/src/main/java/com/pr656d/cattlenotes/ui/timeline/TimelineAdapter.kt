package com.pr656d.cattlenotes.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ItemTimelineBinding
import com.pr656d.cattlenotes.ui.timeline.TimelineActionListener.ItemTimelineSaveData
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.cattlenotes.utils.hideKeyboard
import com.pr656d.cattlenotes.utils.pickADate
import com.pr656d.model.Breeding
import com.pr656d.model.Breeding.BreedingEvent
import com.pr656d.model.Breeding.BreedingEvent.Type
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.utils.TimeUtils
import com.pr656d.shared.utils.nameOrTagNumber
import org.threeten.bp.LocalDate

class TimelineAdapter(
    private val timelineViewModel: TimelineViewModel
) : ListAdapter<BreedingWithCattle, TimelineViewHolder>(BreedingWithCattleDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return TimelineViewHolder(
            ItemTimelineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
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

    private lateinit var breedingEvent: BreedingEvent

    private val breedingEventType: Type
        get() = breedingEvent.type

    private var selectedOption: Boolean? = null
        set(value) = binding.executeAfter {
            selectedOption = value
            field = value
        }

    private var showMoreActions: Boolean = false
        set(value) = binding.executeAfter {
            showMoreActions = value
            field = value
        }

    private var breedingCompleted: Boolean = false
        set(value) = binding.executeAfter {
            breedingCompleted = value
            field = value
        }

    private var doneOn: LocalDate? = null
        set(value) = binding.executeAfter {
            doneOn = value
            field = value
        }

    private var doneOnVisibility: Boolean = false
        set(value) = binding.executeAfter {
            doneOnVisibility = value
            field = value
        }

    fun bind(data: BreedingWithCattle) {
        breedingEvent = data.breeding.nextBreedingEvent ?: return
        this.data = data
        binding.executeAfter {
            // Title : Repeat heat ( `cattle name` ) or Repeat heat ( `Tag number` )
            title = "${breedingEvent.type.displayName} ( ${data.cattle.nameOrTagNumber()} )"
            negativeVisibility = when (breedingEvent.type) {
                Type.DRY_OFF -> false
                Type.CALVING -> false
                else -> true
            }
            showMoreActions = this@TimelineViewHolder.showMoreActions
            doneOn = this@TimelineViewHolder.doneOn
            selectedOption = this@TimelineViewHolder.selectedOption
            breedingCompleted = this@TimelineViewHolder.breedingCompleted
            doneOnVisibility = this@TimelineViewHolder.doneOnVisibility
        }
        initializeListeners()
    }

    /**
     * Use click listener for each radio button to handle radio group selection for once only.
     * Radio group onCheckedChangedListener called multiple times.
     * https://stackoverflow.com/questions/10263778/radiogroup-calls-oncheckchanged-three-times
     */
    private fun onCheckedChanged(@IdRes checkedId: Int, selectedOption: Boolean?) {
        if (this.selectedOption == selectedOption)
            return  // Prevent same option click.

        this.selectedOption = selectedOption

        if (checkedId == binding.radioButtonNeutral.id) {
            onCancelClicked()   // None means cancelled state
            return  // Ignore
        }

        // Show more actions
        showMoreActions = true

        breedingCompleted = when {
            breedingEventType == Type.REPEAT_HEAT && selectedOption == true -> true
            breedingEventType == Type.PREGNANCY_CHECK && selectedOption == false -> true
            else -> false
        }

        doneOnVisibility = when {
            breedingEventType == Type.REPEAT_HEAT && selectedOption == false -> false
            else -> showMoreActions
        }
    }

    private fun onSaveClicked() {
        val newData = newBreedingWithCattle(selectedOption, doneOn)
        listener.saveBreeding(ItemTimelineSaveData(newData, selectedOption))
    }

    private fun onCancelClicked() {
        // Reset selected option
        selectedOption = null

        // Reset done on visibility
        doneOnVisibility = false

        // Reset done on
        doneOn = null

        // Breeding completed to false
        breedingCompleted = false

        // Hide more options
        showMoreActions = false
    }

    private fun initializeListeners() {
        binding.radioButtonNeutral.setOnClickListener {
            onCheckedChanged(it.id, null)
        }

        binding.radioButtonPositive.setOnClickListener {
            onCheckedChanged(it.id, true)
        }

        binding.radioButtonNegative.setOnClickListener {
            onCheckedChanged(it.id, false)
        }

        binding.buttonCancel.setOnClickListener { onCancelClicked() }

        binding.buttonSave.setOnClickListener { onSaveClicked() }

        binding.editTextDoneOn.apply {
            setOnClickListener { v ->
                binding.executeAfter {
                    // Hide keyboard if visible.
                    hideKeyboard(v)
                }

                // Show dialog.
                v.pickADate(
                    onDateCancelled = {
                        binding.executeAfter {
                            isFocusableInTouchMode = false
                        }
                    },
                    onDateSet = { _, dd, mm, yyyy ->
                        // `Month + 1` as it's starting from 0 index and LocalDate index starts from 1.
                        TimeUtils.toLocalDate(dd, mm + 1, yyyy).let {
                            binding.executeAfter {
                                doneOn = it
                                isFocusableInTouchMode = false
                            }
                        }
                    }
                )
            }

            setOnLongClickListener { v ->
                binding.executeAfter {
                    // Hide keyboard if visible.
                    hideKeyboard(v)
                }

                if (!text.isNullOrEmpty()) {
                    // Show dialog.
                    MaterialAlertDialogBuilder(context)
                        .setTitle(context.getString(R.string.remove_text, hint.toString()))
                        .setPositiveButton(R.string.yes) { _, _ ->
                            binding.executeAfter {
                                doneOn = null
                                isFocusableInTouchMode = false
                            }
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            binding.executeAfter {
                                isFocusableInTouchMode = false
                            }
                        }
                        .create()
                        .show()
                }
                true
            }
        }
    }

    private fun newBreedingWithCattle(newStatus: Boolean?, doneOn: LocalDate?): BreedingWithCattle = data.breeding.run {
        when (breedingEventType) {
            Type.REPEAT_HEAT -> BreedingWithCattle(
                data.cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    BreedingEvent(repeatHeat.expectedOn, newStatus, doneOn),
                    pregnancyCheck,
                    dryOff,
                    calving
                )
            )
            Type.PREGNANCY_CHECK -> BreedingWithCattle(
                data.cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    repeatHeat,
                    BreedingEvent(pregnancyCheck.expectedOn, newStatus, doneOn),
                    dryOff,
                    calving
                )
            )
            Type.DRY_OFF -> BreedingWithCattle(
                data.cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    repeat_heat,
                    pregnancyCheck,
                    BreedingEvent(dryOff.expectedOn, newStatus, doneOn),
                    calving
                )
            )
            Type.CALVING -> BreedingWithCattle(
                data.cattle,
                Breeding(
                    cattleId,
                    artificialInsemination,
                    repeat_heat,
                    pregnancyCheck,
                    dryOff,
                    BreedingEvent(calving.expectedOn, newStatus, doneOn)
                )
            )
            Type.UNKNOWN -> throw IllegalStateException("Breeding type can not be UNKNOWN for breeding event ${this.nextBreedingEvent}")
        }.apply {
            // Assign id
            breeding.id = id
        }
    }
}

object BreedingWithCattleDiff : DiffUtil.ItemCallback<BreedingWithCattle>() {
    override fun areItemsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem.cattle.id == newItem.cattle.id && oldItem.breeding.id == newItem.breeding.id

    override fun areContentsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem == newItem
}