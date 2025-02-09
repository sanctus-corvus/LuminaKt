package com.github.sanctuscorvus
import kotlinx.serialization.Serializable

@Serializable
public data class Candidate(
    val content: Content? = null
)