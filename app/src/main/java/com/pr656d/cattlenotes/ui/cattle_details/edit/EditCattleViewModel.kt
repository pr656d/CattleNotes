package com.pr656d.cattlenotes.ui.cattle_details.edit

import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import javax.inject.Inject

class EditCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

}