package com.appcues.action.appcues

import com.appcues.action.ExperienceAction
import com.appcues.data.model.AppcuesConfigMap
import com.appcues.data.model.getConfigInt
import kotlinx.coroutines.delay

internal class DelayAction(
    override val config: AppcuesConfigMap,
) : ExperienceAction {

    companion object {

        const val TYPE = "@appcues/delay"
    }

    private val duration = config.getConfigInt("duration") ?: 0

    override suspend fun execute() {
        delay(duration.toLong())
    }
}
