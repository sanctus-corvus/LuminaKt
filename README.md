# Lumina Gemini Kotlin Client

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![](https://jitpack.io/v/sanctus-corvus/LuminaKt.svg)](https://jitpack.io/#sanctus-corvus/LuminaKt)


**Lumina - Kotlin Client for Google Gemini API**

Gemini Kotlin Client is a Kotlin library designed to facilitate interaction with the Google Gemini API for JVM-based projects.  It provides a straightforward interface for common Gemini API functionalities, such as generating text content, configuring generation parameters, and managing safety settings.

This library is intended for Kotlin developers who need to integrate Gemini's generative AI capabilities into applications like chatbots, content creation tools, and other AI-driven services.  It focuses on ease of use and a clear, Kotlin-idiomatic API.
## ✨ Features

*   **Gemini API Access:** Provides a Kotlin-friendly client for interacting with the Google Gemini API.
*   **Text Generation:** Simplifies text content generation using Gemini models with the `generateContent` function.
*   **Configuration Options:** Allows customization of API key, model, base URL, safety settings, and generation parameters.
*   **Safety Controls:**  Offers fine-grained control over content safety filters (harassment, hate speech, etc.).
*   **Generation Parameters:**  Supports adjusting generation settings like temperature, top-p, and max tokens.
*   **Builders for Configuration:**  Uses `SafetySettingsBuilder` and `GenerationConfigBuilder` for type-safe and readable configuration.
*   **Kotlinx Serialization:**  Leverages kotlinx.serialization for JSON handling.
*   **Lightweight:**  Minimal dependencies and small footprint.
*   **MIT Licensed:**  Open-source library with MIT license.
## 🚀 Getting Started

Integrate the Gemini Kotlin Client into your project in just a few steps:

### 1. Add JitPack Repository

Add the JitPack repository to your project-level `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral() // or other repositories you are using
    maven { url = "https://jitpack.io" }
}
```

### 2. Add Dependency

Add the Gemini Kotlin Client dependency to your module-level `build.gradle.kts` file (e.g., `app/build.gradle.kts`):

```kotlin
dependencies {
    implementation("com.github.sanctus-corvus:LuminaKt:0.1.1")
}
```

### 3. Basic Usage Example

Here's a quick example of how to use the `GeminiClient` to generate content:

```kotlin
import com.github.sanctuscorvus.GeminiClient

fun main() {
    val apiKey = "YOUR_GEMINI_API_KEY"
    val configuration = GeminiClient.Configuration.default(apiKey)
    val geminiClient = GeminiClient(configuration)

    val prompt = "Write a short poem about the beauty of Kotlin."

    val response = geminiClient.generateContent(prompt)

    if (response.statusCode in 200..299) {
        response.body?.candidates?.forEach { candidate ->
            candidate.content?.parts?.forEach { part ->
                println(part.text)
            }
        }
    } else {
        println("Error generating content: ${response.statusCode} - ${response.body}")
    }
}
```

**Remember to replace `"YOUR_GEMINI_API_KEY"` with your actual Gemini API key.**

This example demonstrates:

*   Creating a `Configuration` object with your API key.
*   Initializing a `GeminiClient` instance.
*   Calling `generateContent` with a text prompt.
*   Handling the `HttpResponse` and printing the generated text if successful.

## ⚙️ Configuration

The `GeminiClient` is configured using the `Configuration` class, offering various customization options:

```kotlin
val config = GeminiClient.Configuration.create(
    apiKey = "YOUR_API_KEY", // ✅ API ключ передается как именованный аргумент
    modelName = GeminiClient.GeminiModel.GEMINI_15PRO, // ✅ modelName, если нужно изменить

    safetySettingsBuilder = { 
        blockOnlyHighHarassment() 
        // Add more safety settings using builder methods
    },

    generationConfigBuilder = { 
        temperature(0.7)    
        maxOutputTokens(800)   // Set maximum output tokens
        // Add other generation configurations
    }
)

val geminiClient = GeminiClient(config)
```

Explore the `Configuration` class and its companion object methods (`default`, `withModel`, `defaultSafetySettings`, `relaxedSafetySettings`, `noSafetySettings`, `creativeGenerationConfig`, `preciseGenerationConfig`) for various pre-defined and customizable configurations.

## 🛡️ Safety Settings

Control the safety filters applied to both prompts and generated content using the `SafetySettingsBuilder`. You can block content based on categories like:

*   `HARM_CATEGORY_HARASSMENT`
*   `HARM_CATEGORY_HATE_SPEECH`
*   `HARM_CATEGORY_SEXUALLY_EXPLICIT`
*   `HARM_CATEGORY_DANGEROUS_CONTENT`
*   `HARM_CATEGORY_CIVIC_INTEGRITY`

And set thresholds for blocking:

*   `BLOCK_NONE`
*   `BLOCK_ONLY_HIGH`
*   `BLOCK_MEDIUM_AND_ABOVE`
*   `BLOCK_LOW_AND_ABOVE`

Use the `SafetySettingsBuilder` for fine-grained control:

```kotlin
val safetySettings = GeminiClient.SafetySettingsBuilder()
    .blockMediumAndAboveHateSpeech()
    .blockOnlyHighDangerousContent()
    .build()
```

## 🎨 Generation Configuration

Customize the content generation process with `GenerationConfigBuilder`.  Adjust parameters like:

*   `temperature`: Controls randomness (higher = more creative, lower = more predictable).
*   `topP`: Nucleus sampling (probability cutoff for token selection).
*   `topK`: Top-K sampling (select from top K most likely tokens).
*   `maxOutputTokens`: Maximum number of tokens in the generated response.
*   `stopSequences`: Stop generation when specific sequences are encountered.

Example:

```kotlin
val generationConfig = GeminiClient.GenerationConfigBuilder()
    .temperature(0.8)
    .maxOutputTokens(600)
    .stopSequences("。", "\n\n") // Stop on period or double newline
    .build()
```

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🧑‍💻 Author

Developed and maintained by [Sanctus Corvus](https://github.com/sanctus-corvus)

---

**Enjoy building amazing AI-powered applications with the Gemini Kotlin Client!** ✨

