package com.pr656d.cattlenotes.ui.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentTimelineBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.timeline.TimelineActionListener.OnOptionSelectedData
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.domain.result.EventObserver
import timber.log.Timber
import javax.inject.Inject

class TimelineFragment : NavigationFragment() {

    companion object {
        const val TAG = "TimelineFragment"
        const val TIMELINE_ACTION_UNDO_DURATION: Int = 4_000
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<TimelineViewModel> { viewModelFactory }
    private lateinit var binding: FragmentTimelineBinding
    private val args by navArgs<TimelineFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.breedingId?.let {
            Timber.d("Timeline get the breeding $it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.showUndo.observe(viewLifecycleOwner, EventObserver {
            showUndoSnackbar(it)
        })
    }

    @SuppressLint("WrongConstant")
    private fun showUndoSnackbar(data: OnOptionSelectedData) {
        val breedingWithCattle = data.oldBreedingWithCattle
        val message = breedingWithCattle.undoMessage(data.selectedOption)
        Snackbar
            .make(requireView(), message, Snackbar.LENGTH_LONG)
            .setDuration(TIMELINE_ACTION_UNDO_DURATION)
            .setAction(R.string.undo) {
                model.undoOptionSelected(data)
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    model.saveBreeding(data.newBreedingWithCattle.breeding)
                }
            })
            .show()
    }

    /**
     * Return message like "Repeat heat marked as positive for `name or tagNumber`"
     */
    private fun BreedingWithCattle.undoMessage(selectedOption: Boolean?): String {
        val type = breeding.nextBreedingEvent!!.type.displayName
        val actionName = when(selectedOption) {
            null -> requireContext().getString(R.string.none)
            true -> requireContext().getString(R.string.positive)
            false -> requireContext().getString(R.string.negative)
        }
        val nameOrTagNumber = cattle.name ?: cattle.tagNumber.toString()

        return "$type marked as $actionName of $nameOrTagNumber"
    }
}
