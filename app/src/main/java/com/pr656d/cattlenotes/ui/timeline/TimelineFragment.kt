package com.pr656d.cattlenotes.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentTimelineBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.model.BreedingWithCattle
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class TimelineFragment : NavigationFragment() {

    companion object {
        const val TAG = "TimelineFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val model by viewModels<TimelineViewModel> { viewModelFactory }
    private lateinit var binding: FragmentTimelineBinding

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

        model.showUndo.observe(viewLifecycleOwner, EventObserver { data ->
            val breedingWithCattle = data.oldBreedingWithCattle
            val message = breedingWithCattle.undoMessage(data.selectedOption)
            Snackbar
                .make(requireView(), message, Snackbar.LENGTH_LONG)
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
        })
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
