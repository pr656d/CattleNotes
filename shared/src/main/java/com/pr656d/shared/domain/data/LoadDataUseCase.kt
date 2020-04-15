package com.pr656d.shared.domain.data

import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.domain.UseCase
import javax.inject.Inject

open class LoadDataUseCase @Inject constructor(
    private val dbLoader: DbLoader
) : UseCase<Unit, Unit>() {
    override fun execute(parameters: Unit) {
        dbLoader.load()
    }
}