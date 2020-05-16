package com.pr656d.cattlenotes.ui.milking

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.milking.list.MilkingAdapter
import com.pr656d.cattlenotes.ui.milking.list.MilkingHeadersDecoration
import com.pr656d.cattlenotes.ui.milking.list.MilkingViewModel
import com.pr656d.cattlenotes.utils.clearDecorations
import com.pr656d.model.Milk
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.ZonedDateTime

@BindingAdapter(value = ["milkingItems", "milkingViewModel"], requireAll = true)
fun timelineItems(
    recyclerView: RecyclerView,
    list: List<Milk>?,
    milkingViewModel: MilkingViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter =
            MilkingAdapter(
                milkingViewModel
            )
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as MilkingAdapter).submitList(list)
    }

    // Recreate the decoration used for the sticky date headers
    recyclerView.clearDecorations()
    if (!list.isNullOrEmpty()) {
        recyclerView.addItemDecoration(
            MilkingHeadersDecoration(
                recyclerView.context,
                list
            )
        )
    }
}

@BindingAdapter("milkSourceText")
fun setTextFromMilkSource(view: TextView, source: Milk.Source) {
    val context = view.context ?: return
    view.text = when (source) {
        Milk.Source.Manual -> context.getString(R.string.milk_source_manual)
        Milk.Source.Sms.BGAMAMCS -> context.getString(
            R.string.milk_source_sms,
            context.getString(R.string.bgamamcs)
        )
    }
}

@BindingAdapter("milkTimeText")
fun setMilkTimeText(view: TextView, time: ZonedDateTime) {
    val context = view.context ?: return
    view.text = buildString {
        append(TimeUtils.getDayPartingString(context, time))
        append(" ")
        append(TimeUtils.timeString(time))
    }
}