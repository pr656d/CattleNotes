package com.pr656d.cattlenotes.ui.breeding.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentAddEditBreedingBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.model.Breeding
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class AddEditBreedingFragment : NavigationFragment() {

    companion object {
        const val TAG = "AddBreedingFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddEditBreedingViewModel> { viewModelFactory }
    private val args by navArgs<AddEditBreedingFragmentArgs>()
    private lateinit var binding: FragmentAddEditBreedingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditBreedingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.setCattle(
            Gson().fromJson(args.cattle, Cattle::class.java)
        )

        args.breeding?.let {
            model.setBreeding(
                Gson().fromJson(it, Breeding::class.java)
            )
        }

        model.editing.observe(viewLifecycleOwner) {
            if (it)
                binding.toolbar.setTitle(R.string.edit_breeding)
            else
                binding.toolbar.setTitle(R.string.add_new_breeding)
        }

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
        })

        model.showBackConfirmationDialog.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.back_pressed_message)
                .setMessage(R.string.changes_not_saved_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    model.onBackPressed(true)
                }
                .setNegativeButton(R.string.no, null)
                .create()
                .show()
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            model.onBackPressed()
        }
    }
}