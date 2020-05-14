package com.pr656d.cattlenotes.ui.milking

import com.pr656d.model.Milk

interface MilkingActionListener {
    /**
     * Edit this [milk].
     */
    fun edit(milk: Milk)

    /**
     * Delete this [milk].
     */
    fun delete(milk: Milk)
}