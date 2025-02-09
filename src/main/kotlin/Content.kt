package com.github.sanctuscorvus
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val parts: List<Part>
)