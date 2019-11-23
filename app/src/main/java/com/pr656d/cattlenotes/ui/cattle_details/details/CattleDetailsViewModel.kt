package com.pr656d.cattlenotes.ui.cattle_details.details

import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

}