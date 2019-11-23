package com.pr656d.cattlenotes.ui.cattle_details.edit

import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    fun saveCattle(cattle: Cattle) {
        viewModelScope.launch(Dispatchers.IO) {
            cattleDataRepository.updateCattle(cattle)
        }
    }
}