package com.pr656d.cattlenotes.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.pr656d.cattlenotes.databinding.FragmentTimelineBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import timber.log.Timber
import javax.inject.Inject

class TimelineFragment : NavigationFragment() {

    companion object {
        const val TAG = "TimelineFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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
}
