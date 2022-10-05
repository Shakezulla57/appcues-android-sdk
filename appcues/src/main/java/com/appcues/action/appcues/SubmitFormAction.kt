package com.appcues.action.appcues

import com.appcues.Appcues
import com.appcues.action.ExperienceAction
import com.appcues.action.ExperienceActionQueueTransforming
import com.appcues.analytics.AnalyticsTracker
import com.appcues.analytics.ExperienceLifecycleEvent.StepInteraction
import com.appcues.analytics.ExperienceLifecycleEvent.StepInteraction.InteractionType.FORM_SUBMITTED
import com.appcues.analytics.formattedAsProfileUpdate
import com.appcues.data.model.AppcuesConfigMap
import com.appcues.data.model.getConfigOrDefault
import com.appcues.statemachine.StateMachine

internal class SubmitFormAction(
    override val config: AppcuesConfigMap,
    private val stateMachine: StateMachine,
    private val analyticsTracker: AnalyticsTracker,
) : ExperienceActionQueueTransforming {

    companion object {
        const val TYPE = "@appcues/submit-form"
    }

    private val skipValidation = config.getConfigOrDefault("skipValidation", false)

    // validate form and block future actions if needed
    override fun transformQueue(queue: List<ExperienceAction>, index: Int, appcues: Appcues): List<ExperienceAction> {
        if (skipValidation) {
            return queue
        }

        val experience = stateMachine.state.currentExperience
        val stepIndex = stateMachine.state.currentStepIndex

        if (experience != null && stepIndex != null) {
            val formState = experience.flatSteps[stepIndex].formState

            if (!formState.isFormComplete.value) {
                // remove this action and all subsequent
                return queue.toMutableList().dropLast(queue.count() - index)
            }
        }

        return queue
    }

    // reports analytics for step interaction, for the form submission
    override suspend fun execute(appcues: Appcues) {
        val experience = stateMachine.state.currentExperience
        val stepIndex = stateMachine.state.currentStepIndex

        if (experience != null && stepIndex != null) {
            val formState = experience.flatSteps[stepIndex].formState

            // set user profile attributes to capture the form question/answer
            analyticsTracker.identify(formState.formattedAsProfileUpdate(), interactive = false)

            // track the interaction event
            val interactionEvent = StepInteraction(experience, stepIndex, FORM_SUBMITTED)
            analyticsTracker.track(interactionEvent.name, interactionEvent.properties, interactive = false, isInternal = true)
        }
    }
}