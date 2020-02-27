package com.pr656d.cattlenotes.ui.cattle.addedit.parent

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle

@BindingAdapter(value = ["parentListItems", "parentListViewModel"], requireAll = true)
fun parentListItems(
    recyclerView: RecyclerView,
    list: List<Cattle>?,
    parentListDialogViewModel: ParentListDialogViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = ParentListAdapter(parentListDialogViewModel)
    }

    if (list.isNullOrEmpty()) {
        recyclerView.isVisible = false
    } else {
        recyclerView.isVisible = true
        (recyclerView.adapter as ParentListAdapter).submitList(list)
    }
}

@BindingAdapter("showParentListDivider")
fun showParentListDivider(recyclerView: RecyclerView, value: Boolean) {
    val context = recyclerView.context

    if (value) {
        val dividerItemDecoration = object : RecyclerView.ItemDecoration() {
            val divider = requireNotNull(context.getDrawable(R.drawable.divider))

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) == 0)
                    return

                outRect.top = divider.intrinsicHeight
            }

            override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val ivProfileImage = parent.findViewById<ImageView>(R.id.ivProfileImage)
                val tvTagNumber = parent.findViewById<TextView>(R.id.tvTagNumber)
                val tvName = parent.findViewById<TextView>(R.id.tvName)

                val dividerLeft = parent.paddingStart + ivProfileImage.marginStart +
                        ivProfileImage.width + tvTagNumber.marginStart

                val dividerRight = parent.width - parent.paddingEnd - tvName.marginEnd

                for (i in 0 until parent.childCount - 1) {
                    val child = parent.getChildAt(i)

                    val params = child.layoutParams as RecyclerView.LayoutParams

                    val dividerTop = child.bottom + params.bottomMargin
                    val dividerBottom = dividerTop + divider.intrinsicHeight

                    divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    divider.draw(canvas)
                }
            }
        }

        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}