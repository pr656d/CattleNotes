package com.pr656d.cattlenotes.ui.cattle.addedit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.databinding.FragmentAddEditCattleBinding
import com.pr656d.cattlenotes.shared.domain.result.EventObserver
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.cattle.addedit.parent.ParentListDialogFragment
import com.pr656d.cattlenotes.utils.focus
import com.pr656d.cattlenotes.utils.hideKeyboard
import javax.inject.Inject

class AddEditCattleFragment : NavigationFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
        const val SELECT_PARENT_REQUEST_CODE = 0
        private const val EXTRA_PARENT_TAG_NUMBER = "extra_parent_tag_number"

        /**
         * Use only if you want to pass data back to this fragment by setting this fragment
         * as target fragment.
         */
        fun newIntent(tagNumber: String): Intent = Intent().apply {
            putExtra(EXTRA_PARENT_TAG_NUMBER, tagNumber)
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddEditCattleViewModel> { viewModelFactory }
    private lateinit var binding: FragmentAddEditCattleBinding
    private val args by navArgs<AddEditCattleFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.cattle?.let {
            model.setCattle(Gson().fromJson(it, Cattle::class.java))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditCattleBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.cattle != null)
            binding.toolbar.setTitle(R.string.edit_cattle_details)

        model.selectParent.observe(viewLifecycleOwner, EventObserver { tagNumber ->
            with (binding.editTextParent) {
                hideKeyboard(requireView())
                focus()

                ParentListDialogFragment.newInstance(tagNumber)
                    .apply {
                        setTargetFragment(this@AddEditCattleFragment, SELECT_PARENT_REQUEST_CODE)
                    }
                    .show(parentFragmentManager, TAG)
            }
        })

        model.showBackConfirmationDialog.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.back_pressed_message)
                .setMessage(R.string.changes_not_saved_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    model.navigateUp()
                }
                .setNegativeButton(R.string.no, null)
                .create()
                .show()
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            model.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_PARENT_REQUEST_CODE) {
            when(resultCode) {
                Activity.RESULT_OK -> {
                    data?.extras?.getString(EXTRA_PARENT_TAG_NUMBER)?.let {
                        model.setParent(it)
                    }
                }
            }
            binding.editTextParent.isFocusableInTouchMode = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard(requireView())
    }
}

/** Set helper text as [R.string.required] on [TextInputLayout] based on text provided. */
@BindingAdapter("setRequired")
fun setRequired(view: TextInputLayout, text: String?) {
    view.apply {
        if (text.isNullOrBlank()) {
            isHelperTextEnabled = true
            helperText = context.getString(R.string.required)
        } else {
            isHelperTextEnabled = false
            helperText = null
        }
    }
}