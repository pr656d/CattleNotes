package com.pr656d.cattlenotes.ui.milking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.databinding.FragmentMilkingBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class MilkingFragment : NavigationFragment() {

    companion object {
        const val TAG = "MilkingFragment"
        const val PERMISSION_REQUEST_AT_MILKING = 101
        val PERMISSIONS_REQUIRED = listOf(
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
        )
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<MilkingViewModel> { viewModelFactory }
    private lateinit var binding: FragmentMilkingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMilkingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.setPermissionsGranted(isAllPermissionsGranted())

        model.requestPermissions.observe(viewLifecycleOwner, EventObserver {
            requestPermission()
        })
    }

    private fun checkAndRequestPermission() {
        if (!isAllPermissionsGranted()) requestPermission()
    }

    private fun requestPermission() {
        requestPermissions(getPendingPermissions(), PERMISSION_REQUEST_AT_MILKING)
    }

    private fun getPendingPermissions() =
        PERMISSIONS_REQUIRED.filter { !isPermissionGranted(it) }.toTypedArray()

    private fun isPermissionGranted(permission: String) =
        (ContextCompat.checkSelfPermission(requireActivity(), permission) 
                == PackageManager.PERMISSION_GRANTED)

    /**
     * If empty then all permissions are granted.
     */
    private fun isAllPermissionsGranted() = getPendingPermissions().isEmpty()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_AT_MILKING) {
            model.setPermissionsGranted(
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            )
        }
    }
}
