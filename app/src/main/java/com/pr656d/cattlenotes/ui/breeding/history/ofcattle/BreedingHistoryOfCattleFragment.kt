package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentBreedingHistoryOfCattleBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.breeding.history.ofcattle.BreedingHistoryOfCattleFragmentDirections.Companion.toAddEditBreeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class BreedingHistoryOfCattleFragment : NavigationFragment() {

    companion object {
        const val TAG = "BreedingHistoryFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<BreedingHistoryOfCattleViewModel> { viewModelFactory }

    private val args by navArgs<BreedingHistoryOfCattleFragmentArgs>()

    private lateinit var binding: FragmentBreedingHistoryOfCattleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreedingHistoryOfCattleBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        model.setCattle(
            Gson().fromJson(args.cattle, Cattle::class.java)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchDeleteConfirmation.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_breeding)
                .setPositiveButton(R.string.yes) { _, _ ->
                    model.deleteBreeding(it, deleteConfirmation = true)
                }
                .setNegativeButton(R.string.no, null)
                .create()
                .show()
        })

        model.launchEditBreeding.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                toAddEditBreeding(
                    cattle = Gson().toJson(it.first),
                    breeding = Gson().toJson(it.second)
                )
            )
        })

    }
}