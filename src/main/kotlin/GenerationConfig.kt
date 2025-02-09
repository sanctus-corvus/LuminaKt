package com.github.sanctuscorvus

import kotlinx.serialization.Serializable

@Serializable
public data class GenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val topK: Int? = null,
    val maxOutputTokens: Int? = null,
    val stopSequences: List<String>? = null
)