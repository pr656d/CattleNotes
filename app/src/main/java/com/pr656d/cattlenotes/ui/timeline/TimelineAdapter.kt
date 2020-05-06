package com.pr656d.cattlenotes.ui.timeline

import android.content.Context
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
import com.pr656d.cattlenotes.ui.timeline.TimelineActionListener.ItemTimelineData
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.cattlenotes.utils.hideKeyboard
import com.pr656d.cattlenotes.utils.pickADate
import com.pr656d.model.Breeding.BreedingEvent
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

        val context = binding.root.context

        uiBehaviour = ItemTimelineUiBehaviour(context, data)

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

    private fun onSaveClicked(addNewCattle: Boolean = false) {
        val newData = newBreedingWithCattle(uiBehaviour.selectedOption, uiBehaviour.doneOn)
        listener.saveBreeding(
            ItemTimelineData(newData, uiBehaviour.selectedOption),
            addNewCattle
        )
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

        binding.buttonSaveAndAddNewCattle.setOnClickListener { onSaveClicked(addNewCattle = true) }

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

    private fun newBreedingWithCattle(newStatus: Boolean?, doneOn: LocalDate?): BreedingWithCattle =
        uiBehaviour.data.let { data ->
            when (uiBehaviour.breedingEventType) {
                is BreedingEvent.RepeatHeat -> data.copy(
                    breeding = data.breeding.copy(
                        repeatHeat = data.breeding.repeatHeat.copy(status = newStatus, doneOn =  doneOn)
                    )
                )
                is BreedingEvent.PregnancyCheck -> data.copy(
                    breeding = data.breeding.copy(
                        pregnancyCheck = data.breeding.pregnancyCheck.copy(status = newStatus, doneOn =  doneOn)
                    )
                )
                is BreedingEvent.DryOff -> data.copy(
                    breeding = data.breeding.copy(
                        dryOff = data.breeding.dryOff.copy(status = newStatus, doneOn =  doneOn)
                    )
                )
                is BreedingEvent.Calving -> data.copy(
                    breeding = data.breeding.copy(
                        calving = data.breeding.calving.copy(status = newStatus, doneOn =  doneOn)
                    )
                )
            }
        }

    inner class ItemTimelineUiBehaviour(context: Context, val data: BreedingWithCattle) {
        val breedingEventType = data.breeding.nextBreedingEvent!!

        private val typeDisplayName = context.getString(
            when (breedingEventType) {
                is BreedingEvent.RepeatHeat -> R.string.repeat_heat
                is BreedingEvent.PregnancyCheck -> R.string.pregnancy_check
                is BreedingEvent.DryOff -> R.string.dry_off
                is BreedingEvent.Calving -> R.string.calving
            }
        )

        // Title : Repeat heat ( `cattle name` ) or Repeat heat ( `Tag number` )
        val title = "$typeDisplayName ( ${data.cattle.nameOrTagNumber()} )"

        val negativeVisibility
            get() = when (breedingEventType) {
                is BreedingEvent.DryOff -> false
                is BreedingEvent.Calving -> false
                else -> true
            }

        var showMoreActions = false

        var doneOn: LocalDate? = null

        val doneOnVisibility: Boolean
            get() = when {
                breedingEventType is BreedingEvent.RepeatHeat && selectedOption == false -> false
                else -> showMoreActions
            }

        var selectedOption: Boolean? = null

        val breedingCompleted: Boolean
            get() = when {
                breedingEventType is BreedingEvent.RepeatHeat && selectedOption == true -> true
                breedingEventType is BreedingEvent.PregnancyCheck && selectedOption == false -> true
                breedingEventType is BreedingEvent.Calving && selectedOption == true -> true
                else -> false
            }

        val saveAndAddNewCattleVisibility: Boolean
            get() = breedingEventType is BreedingEvent.Calving && selectedOption == true

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