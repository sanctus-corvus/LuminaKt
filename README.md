# Lumina Gemini Kotlin Client

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![](https://jitpack.io/v/sanctus-corvus/LuminaKt.svg)](https://jitpack.io/#sanctus-corvus/LuminaKt)

**Your Kotlin Gateway to the Power of Gemini AI**

Unlock the potential of Google's cutting-edge Gemini models directly within your Kotlin and JVM projects with the **Gemini Kotlin Client**. This lightweight and intuitive library simplifies interaction with the Gemini API, allowing you to effortlessly integrate powerful generative AI capabilities into your applications.

Whether you're building chatbots, content generation tools, creative writing assistants, or anything in between, the Gemini Kotlin Client provides a seamless and developer-friendly experience.

## ✨ Key Features

*   **Effortless Gemini API Integration:**  Provides a clean and Kotlin-idiomatic interface for interacting with the Gemini API.
*   **Content Generation Made Simple:**  Easily generate text-based content with the `generateContent` function, leveraging the power of Gemini models.
*   **Flexible Configuration:**  Customize your client with API key, model selection (including Gemini Pro and more), and base API URL.
*   **Safety Settings Control:**  Fine-tune safety settings to control content filtering based on categories like harassment, hate speech, and more.
*   **Generation Configuration Options:**  Adjust generation parameters like temperature, top-p, top-k, and max output tokens for creative or precise text generation.
*   **Builder Pattern for Settings:**  Utilize intuitive `SafetySettingsBuilder` and `GenerationConfigBuilder` for easy and readable configuration of safety and generation parameters.
*   **Kotlinx Serialization:**  Uses kotlinx.serialization for efficient and type-safe JSON handling.
*   **Lightweight and Dependency-Friendly:**  Minimal dependencies, designed to integrate smoothly into your Kotlin and JVM projects.
*   **Open Source & MIT Licensed:**  Free to use and modify, fostering community contributions and growth.

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
    implementation("com.github.sanctus-corvus:LuminaKt:0.1")
}
```

### 3. Basic Usage Example

Here's a quick example of how to use the `GeminiClient` to generate content:

```kotlin
import com.github.sanctuscorvus.GeminiClient
import com.github.sanctuscorvus.Configuration

fun main() {
    val apiKey = "YOUR_GEMINI_API_KEY" // 🔑  Replace with your actual Gemini API Key
    val configuration = Configuration.default(apiKey) // Create default configuration with your API key
    val geminiClient = GeminiClient(configuration)

    val prompt = "Write a short poem about the beauty of Kotlin."

    val response = geminiClient.generateContent(prompt)

    if (response.isSuccessful()) {
        response.body()?.candidates?.forEach { candidate ->
            candidate.content.parts.forEach { part ->
                println(part.text)
            }
        }
    } else {
        println("Error generating content: ${response.statusCode()} - ${response.body()}")
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
val configuration = Configuration.create(apiKey = "YOUR_API_KEY") {
    modelName = GeminiClient.GeminiModel.GEMINI_15PRO // Choose your Gemini model
    apiBaseUrl = "https://your-custom-api-url.com/v1beta/models/${modelName}" // Customize API base URL (if needed)

    safetySettingsBuilder {
        blockOnlyHighHarassment() // Relaxed safety setting for harassment
        // Add more safety settings using builder methods
    }

    generationConfigBuilder {
        temperature(0.7)      // Adjust temperature for creativity
        maxOutputTokens(800)   // Set maximum output tokens
        // Add other generation configurations
    }
}

val geminiClient = GeminiClient(configuration)
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

