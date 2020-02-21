package com.pr656d.cattlenotes.ui.breeding.addedit

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.databinding.FragmentAddEditBreedingBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.utils.EventObserver
import com.pr656d.cattlenotes.utils.showDialog
import javax.inject.Inject

class AddEditBreedingFragment : NavigationFragment() {

    companion object {
        const val TAG = "AddBreedingFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddEditBreedingViewModel> { viewModelFactory }
    private val args by navArgs<AddEditBreedingFragmentArgs>()
    private lateinit var binding: FragmentAddEditBreedingBinding
    private val progressDialog by lazy {
        @Suppress("DEPRECATION")
        ProgressDialog(requireContext()).apply {
            setMessage(requireContext().getString(R.string.saving_dialog_message))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditBreedingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            cattle = Gson().fromJson(args.cattle, Cattle::class.java)
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.saving.observe(viewLifecycleOwner) {
            if (it)
                progressDialog.show()
            else
                progressDialog.hide()
        }

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.fabButtonSaveBreeding.id)
                .show()
        })

        model.showBackConfirmationDialog.observe(viewLifecycleOwner, EventObserver {
            requireContext().showDialog(
                title = R.string.back_pressed_message,
                message = R.string.changes_not_saved_message,
                positiveTextId = R.string.yes,
                onPositiveSelected = { model.navigateUp() },
                negativeTextId = R.string.no
            )
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            model.onBackPressed()
        }
    }
}

@BindingAdapter("checkedButton")
fun setCheckedButton(radioGroup: RadioGroup, state: Boolean?) {
    val checkedButtonId = when (state) {
        null -> R.id.rbRepeatHeatStatusNone
        true -> R.id.rbRepeatHeatStatusPositive
        else -> R.id.rbRepeatHeatStatusNegative
    }
    if (radioGroup.checkedRadioButtonId != checkedButtonId)
        radioGroup.check(checkedButtonId)
}

@InverseBindingAdapter(attribute = "checkedButton")
fun getCheckedButton(radioGroup: RadioGroup): Boolean? {
    return when(radioGroup.checkedRadioButtonId) {
        R.id.rbRepeatHeatStatusPositive -> true
        R.id.rbRepeatHeatStatusNegative -> false
        else -> null
    }
}

@BindingAdapter(value = ["onCheckedChanged", "checkedButtonAttrChanged"], requireAll = false)
fun setOnCheckedChangeListener(
    radioGroup: RadioGroup,
    listener: RadioGroup.OnCheckedChangeListener?,
    attrChange: InverseBindingListener?
) {
    if (attrChange == null) {
        radioGroup.setOnCheckedChangeListener(listener)
    } else {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            listener?.onCheckedChanged(group, checkedId)
            attrChange.onChange()
        }
    }
}