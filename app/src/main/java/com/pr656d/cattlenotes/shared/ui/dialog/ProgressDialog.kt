package com.pr656d.cattlenotes.shared.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.navigation.fragment.navArgs
import com.pr656d.cattlenotes.R
import kotlinx.android.synthetic.main.progress_dialog.view.*
import javax.inject.Inject

class ProgressDialog @Inject constructor() : AppCompatDialogFragment() {
    private val args by navArgs<ProgressDialogArgs>()

    init {
        isCancelable = false
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.progress_dialog, null)
        view.tvMessage.setText(args.message)
        return AlertDialog.Builder(activity).setView(view).create()
    }
}