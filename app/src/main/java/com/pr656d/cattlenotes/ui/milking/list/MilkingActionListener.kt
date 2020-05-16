package com.pr656d.cattlenotes.ui.milking.list

import com.pr656d.model.Milk

interface MilkingActionListener {
    /**
     * Delete this [milk].
     */
    fun delete(milk: Milk)
}