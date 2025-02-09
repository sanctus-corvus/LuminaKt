package com.github.sanctuscorvus

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting>? = null,
    val generationConfig: GenerationConfig? = null
)