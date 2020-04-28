package com.pr656d.cattlenotes.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
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

    private lateinit var uiBehaviour: ItemTimelineUiBehaviour

    fun bind(data: BreedingWithCattle) {
        data.breeding.nextBreedingEvent ?: return
        uiBehaviour = ItemTimelineUiBehaviour(data)
        binding.executeAfter {
            uiBehaviour = this@TimelineViewHolder.uiBehaviour
        }
        initializeListeners()
    }

    /**
     * Use click listener for each radio button to handle radio group selection for once only.
     * Radio group onCheckedChangedListener called multiple times.
     * https://stackoverflow.com/questions/10263778/radiogroup-calls-oncheckchanged-three-times
     */
    private fun onCheckedChanged(@IdRes checkedId: Int, selectedOption: Boolean?) {
        if (uiBehaviour.selectedOption == selectedOption)
            return  // Prevent same option click.

        uiBehaviour.selectedOption = selectedOption

        if (checkedId == binding.radioButtonNeutral.id) {
            onCancelClicked()   // None means cancelled state
            return  // Ignore
        }

        // Show more actions
        uiBehaviour.showMoreActions = true

        uiBehaviour.bind()
    }

    private fun onSaveClicked() {
        val newData = newBreedingWithCattle(uiBehaviour.selectedOption, uiBehaviour.doneOn)
        listener.saveBreeding(ItemTimelineSaveData(newData, uiBehaviour.selectedOption))
    }

    private fun onCancelClicked() {
        // Reset selected option
        uiBehaviour.selectedOption = null

        // Reset done on
        uiBehaviour.doneOn = null

        // Hide more options
        uiBehaviour.showMoreActions = false

        uiBehaviour.bind()
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
                    onDateSet = { _, dd, mm, yyyy ->
                        // `Month + 1` as it's starting from 0 index and LocalDate index starts from 1.
                        TimeUtils.toLocalDate(dd, mm + 1, yyyy).let {
                            uiBehaviour.doneOn = it
                            uiBehaviour.bind()
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
                            uiBehaviour.doneOn = null
                            uiBehaviour.bind()
                        }
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show()
                }
                true
            }
        }
    }

    private fun newBreedingWithCattle(newStatus: Boolean?, doneOn: LocalDate?): BreedingWithCattle {
        return uiBehaviour.data.breeding.run {
            when (uiBehaviour.breedingEventType) {
                Type.REPEAT_HEAT -> BreedingWithCattle(
                    uiBehaviour.data.cattle,
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
                    uiBehaviour.data.cattle,
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
                    uiBehaviour.data.cattle,
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
                    uiBehaviour.data.cattle,
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

    inner class ItemTimelineUiBehaviour(val data: BreedingWithCattle) {
        val breedingEventType = data.breeding.nextBreedingEvent!!.type

        // Title : Repeat heat ( `cattle name` ) or Repeat heat ( `Tag number` )
        val title = "${breedingEventType.displayName} ( ${data.cattle.nameOrTagNumber()} )"

        val negativeVisibility
            get() = when (breedingEventType) {
                Type.DRY_OFF -> false
                Type.CALVING -> false
                else -> true
            }

        var showMoreActions = false

        var doneOn: LocalDate? = null

        val doneOnVisibility: Boolean
            get() = when {
                breedingEventType == Type.REPEAT_HEAT && selectedOption == false -> false
                else -> showMoreActions
            }

        var selectedOption: Boolean? = null

        val breedingCompleted: Boolean
            get() = when {
                breedingEventType == Type.REPEAT_HEAT && selectedOption == true -> true
                breedingEventType == Type.PREGNANCY_CHECK && selectedOption == false -> true
                else -> false
            }

        fun bind() {
            val parent = itemView.parent as? ViewGroup ?: return
            TransitionManager.beginDelayedTransition(parent, AutoTransition())
            binding.executeAfter {
                uiBehaviour = this@ItemTimelineUiBehaviour
            }
        }
    }
}

object BreedingWithCattleDiff : DiffUtil.ItemCallback<BreedingWithCattle>() {
    override fun areItemsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem.cattle.id == newItem.cattle.id && oldItem.breeding.id == newItem.breeding.id

    override fun areContentsTheSame(oldItem: BreedingWithCattle, newItem: BreedingWithCattle) =
        oldItem == newItem
}