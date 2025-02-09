package com.github.sanctuscorvus
import kotlinx.serialization.Serializable

@Serializable
data class Candidate(
    val content: Content? = null
)