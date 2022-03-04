package com.appcues.statemachine.states

import com.appcues.data.model.Experience
import com.appcues.statemachine.Action
import com.appcues.statemachine.Action.RenderStep
import com.appcues.statemachine.State
import com.appcues.statemachine.State.Transition

internal class BeginningStep(
    override val experience: Experience,
    val step: Int
) : State {
    override fun handleAction(action: Action): Transition? {
        return when (action) {
            is RenderStep -> {
                // this transition is triggered by "callback" from AppcuesActivity (via VM)
                // to tell us that the view has rendered

                // no additional work to do, just update state
                Transition(RenderingStep(experience, step))
            }
            else -> null
        }
    }
}