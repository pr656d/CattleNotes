package com.pr656d.cattlenotes.ui.cattle.list

import com.pr656d.cattlenotes.data.model.Cattle

interface CattleActionListener {
    fun openCattle(cattle: Cattle)
}