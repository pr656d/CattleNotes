package com.pr656d.shared.domain.milk.sms

import com.pr656d.model.Milk
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.domain.internal.SyncScheduler
import javax.inject.Inject

open class GetAvailableMilkSmsSourcesUseCase @Inject constructor()
    : UseCase<Unit, List<Milk.Source.Sms>>() {
    init {
        taskScheduler = SyncScheduler
    }
    override fun execute(parameters: Unit): List<Milk.Source.Sms> {
        return Milk.Source.Sms.INSTANCES.values.toList()
    }
}