package com.appcues.data.remote.response.step

import com.appcues.data.remote.response.action.ActionResponse
import com.appcues.data.remote.response.trait.TraitResponse
import java.util.UUID

internal data class StepResponse(
    val id: UUID,
    val contentType: String,
    val content: StepContentResponse,
    val traits: List<TraitResponse>,
    val actions: HashMap<String, List<ActionResponse>>
)