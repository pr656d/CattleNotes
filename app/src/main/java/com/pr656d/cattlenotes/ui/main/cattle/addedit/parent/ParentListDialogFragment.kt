package com.pr656d.cattlenotes.ui.main.cattle.addedit.parent

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentParentListBinding
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleFragment
import com.pr656d.cattlenotes.utils.EventObserver
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ParentListDialogFragment : DaggerDialogFragment() {

    companion object {
        const val TAG = "ParentListDialogFragment"
        private const val ARG_TAG_NUMBER = "arg_tag_number"

        fun newInstance(value: String): ParentListDialogFragment {
            val args = Bundle().apply { putString(ARG_TAG_NUMBER, value) }
            return ParentListDialogFragment().apply {
                arguments = args
            }
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val model by viewModels<ParentListDialogViewModel> { viewModelFactory }
    private lateinit var binding: FragmentParentListBinding

    override fun onStart() {
        super.onStart()

        requireDialog().window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setWindowAnimations(R.style.AppTheme_DialogAnim)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        binding.toolbar.setNavigationOnClickListener { dismiss() }

        val tagNumber = requireNotNull(arguments).getString(ARG_TAG_NUMBER)
        tagNumber?.let { model.setTagNumber(tagNumber.toLong()) }

        model.parentSelected.observe(viewLifecycleOwner, EventObserver {
            setParentTagNumber(it.tagNumber.toString())
            dismiss()
        })

        return binding.root
    }

    private fun setParentTagNumber(parentTagNumber: String) {
        targetFragment?.run {
            val intent = AddEditCattleFragment.newIntent(parentTagNumber)
            onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        targetFragment?.run {
            onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, Intent())
        }
        super.onDismiss(dialog)
    }
}