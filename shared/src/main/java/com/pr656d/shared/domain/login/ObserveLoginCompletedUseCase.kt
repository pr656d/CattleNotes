package com.pr656d.shared.domain.login

import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

open class ObserveLoginCompletedUseCase  @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : MediatorUseCase<Unit, Boolean>() {
    override fun execute(parameters: Unit) {
        result.addSource(preferenceStorage.observableLoginCompleted) {
            result.postValue(Result.Success(it))
        }
    }
}