package com.pr656d.cattlenotes.ui.milking

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.pr656d.cattlenotes.R
import com.pr656d.model.Milk

@BindingAdapter("milkSourceText")
fun setTextFromMilkSource(view: TextView, source: Milk.Source) {
    val context = view.context ?: return
    view.text = when (source) {
        Milk.Source.Manual -> context.getString(R.string.sms_source_manual)
        Milk.Source.Sms.BGAMAMCS -> context.getString(
            R.string.sms_source_sms,
            context.getString(R.string.bgamamcs)
        )
    }
}