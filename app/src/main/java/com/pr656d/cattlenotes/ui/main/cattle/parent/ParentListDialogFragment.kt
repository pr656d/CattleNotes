package com.pr656d.cattlenotes.ui.main.cattle.parent

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.pr656d.cattlenotes.R
import kotlinx.android.synthetic.main.fragment_parent_list.*
import javax.inject.Inject

class ParentListDialogFragment @Inject constructor() : DialogFragment() {

    companion object {
        const val TAG = "ParentListDialogFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var listenerFragment: ParentSelector

    init {
        isCancelable = false
    }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listenerFragment = targetFragment as ParentSelector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_parent_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel by viewModels<ParentListViewModel> { viewModelFactory }

        viewModel.setCurrentTagNumber(listenerFragment.provideCurrentTagNumber())

        val parentAdapter = ParentCattleListAdapter(
            object: ParentCattleListAdapter.ClickListener {
                override fun onClick(parentTagNumber: String) {
                    listenerFragment.onParentSelected(parentTagNumber)
                }
            }
        )

        rvParentList.run {
            adapter = parentAdapter
        }

        viewModel.parentList.observe(viewLifecycleOwner) {
            parentAdapter.updateList(it)

            if (it.isEmpty()) {
                rvParentList.visibility = View.GONE
                tvEmptyListMessage.visibility = View.VISIBLE
            } else {
                tvEmptyListMessage.visibility = View.GONE
                rvParentList.visibility = View.VISIBLE
            }
            progressBar.visibility = View.GONE
        }

        selectParentToolbar.setNavigationOnClickListener {
            listenerFragment.parentDialogCancelled()
        }
    }

    interface ParentSelector {
        /**
         * Target fragment will be get callback with parent tag number.
         */
        fun onParentSelected(parentTagNumber: String)

        /**
         * Target fragment will get callback for cancellation.
         */
        fun parentDialogCancelled()

        /**
         * Get current tag number to eliminate from the list.
         * It's doesn't make any sense if a animal chooses it self as it's parent.
         */
        fun provideCurrentTagNumber(): Long?
    }
}