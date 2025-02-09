package com.github.sanctuscorvus

public enum class SafetyThreshold(public val value: String) {
    BLOCK_NONE("BLOCK_NONE"),
    BLOCK_ONLY_HIGH("BLOCK_ONLY_HIGH"),
    BLOCK_MEDIUM_AND_ABOVE("BLOCK_MEDIUM_AND_ABOVE"),
    BLOCK_LOW_AND_ABOVE("BLOCK_LOW_AND_ABOVE")
}