package com.appcues.data.mapper.experience

import android.content.Context
import com.appcues.action.ExperienceAction
import com.appcues.action.appcues.LaunchExperienceAction
import com.appcues.action.appcues.LinkAction
import com.appcues.data.mapper.LeveledTraitResponse
import com.appcues.data.mapper.action.ActionsMapper
import com.appcues.data.mapper.mergeTraits
import com.appcues.data.mapper.step.StepMapper
import com.appcues.data.mapper.trait.TraitsMapper
import com.appcues.data.model.Experience
import com.appcues.data.model.ExperiencePriority
import com.appcues.data.model.ExperiencePriority.NORMAL
import com.appcues.data.model.Experiment
import com.appcues.data.model.StepContainer
import com.appcues.data.remote.response.ExperimentResponse
import com.appcues.data.remote.response.experience.ExperienceResponse
import com.appcues.data.remote.response.experience.FailedExperienceResponse
import com.appcues.data.remote.response.experience.LossyExperienceResponse
import com.appcues.data.remote.response.step.StepContainerResponse
import com.appcues.trait.BackdropDecoratingTrait
import com.appcues.trait.ContainerDecoratingTrait
import com.appcues.trait.ContentHolderTrait
import com.appcues.trait.ContentWrappingTrait
import com.appcues.trait.ExperienceTrait
import com.appcues.trait.ExperienceTraitLevel.EXPERIENCE
import com.appcues.trait.ExperienceTraitLevel.GROUP
import com.appcues.trait.PresentingTrait
import com.appcues.trait.appcues.DefaultContentHolderTrait
import com.appcues.trait.appcues.DefaultPresentingTrait
import org.koin.core.scope.Scope
import java.util.UUID

internal class ExperienceMapper(
    private val stepMapper: StepMapper,
    private val traitsMapper: TraitsMapper,
    private val actionsMapper: ActionsMapper,
    private val scope: Scope,
    private val context: Context,
) {
    // this version is used to map all decoded response objects, which may represent real Experiences
    // or failed responses with minimal info for error reporting
    fun mapDecoded(
        from: LossyExperienceResponse,
        priority: ExperiencePriority = NORMAL,
        experiments: List<ExperimentResponse>? = null,
        requestId: UUID? = null,
    ): Experience {
        return when (from) {
            is ExperienceResponse -> map(from, priority, experiments, requestId)
            is FailedExperienceResponse -> mapFailed(from, priority, experiments, requestId)
        }
    }

    // this version makes a synthetic Experience from the failure data, with just enough info
    // to report the flow issue about deserialization in the `error` field, for troubleshooting
    private fun mapFailed(
        from: FailedExperienceResponse,
        priority: ExperiencePriority = NORMAL,
        experiments: List<ExperimentResponse>? = null,
        requestId: UUID? = null,
    ): Experience {
        return Experience(
            id = from.id,
            name = from.name ?: "",
            stepContainers = emptyList(),
            published = true,
            priority = priority,
            type = from.type ?: "",
            publishedAt = from.publishedAt,
            experiment = experiments?.getExperiment(from.id),
            completionActions = emptyList(),
            requestId = requestId,
            error = from.error
        )
    }

    // this version maps the normal successful ExperienceResponse
    fun map(
        from: ExperienceResponse,
        priority: ExperiencePriority = NORMAL,
        experiments: List<ExperimentResponse>? = null,
        requestId: UUID? = null,
    ): Experience {
        val experienceTraits = from.traits.map { it to EXPERIENCE }
        return Experience(
            id = from.id,
            name = from.name,
            stepContainers = from.steps.mapToStepContainer(experienceTraits),
            published = from.state != "DRAFT", // "DRAFT" is used for experience preview in builder
            priority = priority,
            type = from.type,
            publishedAt = from.publishedAt,
            experiment = experiments?.getExperiment(from.id),
            completionActions = arrayListOf<ExperienceAction>().apply {
                from.redirectUrl?.let { add(LinkAction(it, scope.get())) }
                from.nextContentId?.let { add(LaunchExperienceAction(it)) }
            },
            requestId = requestId,
        )
    }

    private fun List<StepContainerResponse>.mapToStepContainer(
        experienceTraits: List<LeveledTraitResponse>,
    ) = map { stepContainerResponse ->
        val containerTraits = stepContainerResponse.traits.map { it to GROUP }
        val mergedTraits = containerTraits.mergeTraits(experienceTraits)
        val mappedTraits = traitsMapper.map(mergedTraits)

        // this is where we will use groups to organize steps into stepContainers
        // also merge all necessary traits for each step
        StepContainer(
            id = stepContainerResponse.id,
            steps = stepContainerResponse.children.map { step -> stepMapper.map(step, mergedTraits) },
            actions = actionsMapper.map(stepContainerResponse.actions),
            presentingTrait = mappedTraits.getExperiencePresentingTraitOrDefault(),
            contentHolderTrait = mappedTraits.getContainerCreatingTraitOrDefault(),
            // what should we do if no content wrapping trait is found?
            contentWrappingTrait = mappedTraits.filterIsInstance<ContentWrappingTrait>().first(),
            backdropDecoratingTraits = mappedTraits.filterIsInstance<BackdropDecoratingTrait>(),
            containerDecoratingTraits = mappedTraits.filterIsInstance<ContainerDecoratingTrait>(),
        )
    }

    private fun List<ExperienceTrait>.getContainerCreatingTraitOrDefault(): ContentHolderTrait {
        return filterIsInstance<ContentHolderTrait>().firstOrNull() ?: DefaultContentHolderTrait(null)
    }

    private fun List<ExperienceTrait>.getExperiencePresentingTraitOrDefault(): PresentingTrait {
        return filterIsInstance<PresentingTrait>().firstOrNull() ?: DefaultPresentingTrait(null, scope, context)
    }

    private fun List<ExperimentResponse>.getExperiment(experienceId: UUID) =
        this.firstOrNull { it.experienceId == experienceId }?.let { experimentResponse ->
            Experiment(
                id = experimentResponse.experimentId,
                group = experimentResponse.group,
                experienceId = experimentResponse.experienceId,
                goalId = experimentResponse.goalId,
                contentType = experimentResponse.contentType
            )
        }
}
