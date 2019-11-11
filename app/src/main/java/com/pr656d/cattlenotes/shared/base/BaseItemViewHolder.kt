package com.pr656d.cattlenotes.shared.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.pr656d.cattlenotes.shared.utils.common.inflate

abstract class BaseItemViewHolder<T : Any>(
    @LayoutRes layoutId: Int, parent: ViewGroup
) : RecyclerView.ViewHolder(parent.inflate(layoutId)) {

    init {
        onCreate()
    }

    private fun onCreate() {
        setupView(itemView)
    }

    abstract fun bind(data: T)

    abstract fun setupView(view: View)
}