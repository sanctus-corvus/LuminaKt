package com.github.sanctuscorvus

import kotlinx.serialization.Serializable

@Serializable
public data class GeminiResponse(
    val candidates: List<Candidate>? = null
)