package com.github.sanctuscorvus
import kotlinx.serialization.Serializable

@Serializable
public data class Content(
    val parts: List<Part>
)