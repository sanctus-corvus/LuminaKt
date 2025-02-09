package com.github.sanctuscorvus

import kotlinx.serialization.Serializable

@Serializable
public data class SafetySetting(
    val category: String,
    val threshold: String
)