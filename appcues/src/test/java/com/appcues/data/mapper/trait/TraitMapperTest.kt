package com.appcues.data.mapper.trait

import com.appcues.data.remote.response.trait.TraitResponse
import com.google.common.truth.Truth
import org.junit.Test

class TraitMapperTest {

    private val mapper = TraitMapper()

    @Test
    fun `map SHOULD map TraitResponse into Trait`() {
        // Given
        val from = TraitResponse(
            type = "Trait Type",
        )
        // When
        val result = mapper.map(from)
        // Then
        with(result) {
            Truth.assertThat(type).isEqualTo("Trait Type")
        }
    }
}