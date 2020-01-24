package com.pr656d.cattlenotes.ui.main.cattle.add

import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.ui.main.cattle.base.BaseCattleViewModel
import javax.inject.Inject

class AddCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseCattleViewModel() {

    override fun provideCattleDataRepository(): CattleDataRepository =  cattleDataRepository

    override fun provideCurrentTagNumber(): Long? = null

    fun onSaveClick() = saveCattle(
        doOnSuccess = {
            navigateUp()
        }
    )
}