package com.github.sanctuscorvus
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse as JavaHttpResponse
import java.net.http.HttpRequest.BodyPublishers
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * Клиент для взаимодействия с Gemini API.
 * Инкапсулирует логику отправки запросов и обработки ответов.
 * Конфигурация клиента задается через класс Configuration.
 */
class GeminiClient(configuration: Configuration) {

    private val apiKey: String = configuration.apiKey
    private val apiBaseUrl: String = configuration.apiBaseUrl
    private val defaultSafetySettings: List<SafetySetting>? = configuration.defaultSafetySettings
    private val defaultGenerationConfig: GenerationConfig? = configuration.defaultGenerationConfig

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Отправляет POST запрос к API.
     * @param apiUrl URL API endpoint.
     * @param requestBody Тело запроса, сериализуемое в JSON.
     * @param requestSerializer Сериализатор Kotlinx Serialization для тела запроса.
     * @param responseDeserializer Десериализатор Kotlinx Serialization для тела ответа.
     * @param headers Дополнительные HTTP заголовки.
     * @return HttpResponse с кодом статуса и десериализованным телом ответа.
     */
    private fun <TRequest : Any, TResponse : Any> postRequest(
        apiUrl: String,
        requestBody: TRequest,
        requestSerializer: kotlinx.serialization.KSerializer<TRequest>,
        responseDeserializer: kotlinx.serialization.KSerializer<TResponse>,
        headers: Map<String, String>? = null
    ): HttpResponse<TResponse> {
        val client = HttpClient.newHttpClient()
        val fullApiUrl = "$apiUrl?key=$apiKey"
        val jsonData = json.encodeToString(requestSerializer, requestBody)

        val builder = HttpRequest.newBuilder()
            .uri(URI.create(fullApiUrl))
            .POST(BodyPublishers.ofString(jsonData))
            .header("Content-Type", "application/json")

        headers?.forEach { (name, value) ->
            builder.header(name, value)
        }

        val request = builder.build()

        return try {
            val response: JavaHttpResponse<String> = client.send(request, JavaHttpResponse.BodyHandlers.ofString())
            val responseBodyParsed: TResponse? = response.body()?.let {
                json.decodeFromString(responseDeserializer, it)
            }
            HttpResponse(response.statusCode(), responseBodyParsed)
        } catch (e: Exception) {
            HttpResponse(-1, null)
        }
    }

    /**
     * Генерирует контент, используя Gemini API.
     * @param prompt Текстовый запрос.
     * @param safetySettings Настройки безопасности для запроса (переопределяют дефолтные, если заданы).
     * @param generationConfig Параметры конфигурации генерации (переопределяют дефолтные, если заданы).
     * @return HttpResponse с кодом статуса и ответом Gemini API.
     */
    fun generateContent(
        prompt: String,
        safetySettings: List<SafetySetting>? = null,
        generationConfig: GenerationConfig? = null
    ): HttpResponse<GeminiResponse> {
        val requestSafetySettings = safetySettings ?: defaultSafetySettings
        val requestGenerationConfig = generationConfig ?: defaultGenerationConfig

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            ),
            safetySettings = requestSafetySettings,
            generationConfig = requestGenerationConfig
        )

        return postRequest(
            apiUrl = "$apiBaseUrl:generateContent",
            requestBody = request,
            requestSerializer = GeminiRequest.serializer(),
            responseDeserializer = GeminiResponse.serializer()
        )
    }


    /**
     * Конфигурация клиента GeminiClient.
     * Позволяет задать API ключ, модель, базовый URL и настройки по умолчанию.
     * Используйте Configuration.create(...) для создания экземпляра конфигурации.
     */
    data class Configuration(
        val apiKey: String,
        val modelName: String = GeminiModel.GEMINI_15PRO.modelName,
        val apiBaseUrl: String = "https://generativelanguage.googleapis.com/v1beta/models/$modelName",
        val defaultSafetySettings: List<SafetySetting>? = defaultSafetySettings(),
        val defaultGenerationConfig: GenerationConfig? = defaultGenerationConfig()
    ) {
        companion object {
            /**
             * Создает конфигурацию GeminiClient с заданным API ключом и настройками.
             * Используйте лямбда-функции `safetySettingsBuilder` и `generationConfigBuilder` для кастомизации настроек.
             * @param apiKey API ключ Gemini.
             * @param modelName Имя модели Gemini (по умолчанию Gemini Pro).
             * @param safetySettingsBuilder Лямбда-функция для настройки SafetySettingsBuilder.
             * @param generationConfigBuilder Лямбда-функция для настройки GenerationConfigBuilder.
             * @return Экземпляр Configuration.
             */
            fun create(
                apiKey: String,
                modelName: GeminiModel = GeminiModel.GEMINI_15PRO,
                safetySettingsBuilder: SafetySettingsBuilder.() -> Unit = {},
                generationConfigBuilder: GenerationConfigBuilder.() -> Unit = {}
            ): Configuration = Configuration(
                apiKey = apiKey,
                modelName = modelName.modelName,
                apiBaseUrl = "https://generativelanguage.googleapis.com/v1beta/models/${modelName.modelName}",
                defaultSafetySettings = SafetySettingsBuilder().apply(safetySettingsBuilder).build(),
                defaultGenerationConfig = GenerationConfigBuilder().apply(generationConfigBuilder).build()
            )

            /**
             * Создает конфигурацию с дефолтными настройками, только API ключ обязателен.
             * Модель по умолчанию - Gemini Pro.
             * @param apiKey API ключ Gemini.
             * @return Экземпляр Configuration.
             */
            fun default(apiKey: String) = Configuration(apiKey = apiKey)

            /**
             * Создает конфигурацию с указанной моделью Gemini и дефолтными настройками.
             * @param apiKey API ключ Gemini.
             * @param modelName Модель Gemini для использования.
             * @return Экземпляр Configuration.
             */
            fun withModel(apiKey: String, modelName: GeminiModel) =
                Configuration(apiKey = apiKey, modelName = modelName.modelName)

            /**
             * Возвращает дефолтные настройки безопасности (блокировка на среднем уровне и выше для всех категорий).
             * @return Список SafetySetting.
             */
            fun defaultSafetySettings(): List<SafetySetting> = SafetySettingsBuilder()
                .blockMediumAndAboveHarassment()
                .blockMediumAndAboveHateSpeech()
                .blockMediumAndAboveSexuallyExplicit()
                .blockMediumAndAboveDangerousContent()
                .blockMediumAndAboveCivicIntegrity()
                .build()

            /**
             * Возвращает "мягкие" настройки безопасности (блокировка только на высоком уровне для всех категорий).
             * @return Список SafetySetting.
             */
            fun relaxedSafetySettings(): List<SafetySetting> = SafetySettingsBuilder()
                .blockOnlyHighHarassment()
                .blockOnlyHighHateSpeech()
                .blockOnlyHighSexuallyExplicit()
                .blockOnlyHighDangerousContent()
                .blockOnlyHighCivicIntegrity()
                .build()

            /**
             * Возвращает пустой список Safety Settings, отключая фильтрацию безопасности.
             * @return Пустой список SafetySetting.
             */
            fun noSafetySettings(): List<SafetySetting> = emptyList()

            /**
             * Возвращает конфигурацию генерации, настроенную на креативность (высокая температура, top-p, top-k).
             * @return GenerationConfig.
             */
            fun creativeGenerationConfig(): GenerationConfig = GenerationConfigBuilder()
                .temperature(0.9)
                .topP(0.9)
                .topK(30)
                .maxOutputTokens(1000)
                .build()

            /**
             * Возвращает конфигурацию генерации, настроенную на точность (низкая температура, top-p, top-k).
             * @return GenerationConfig.
             */
            fun preciseGenerationConfig(): GenerationConfig = GenerationConfigBuilder()
                .temperature(0.2)
                .topP(0.3)
                .topK(5)
                .maxOutputTokens(500)
                .build()


            /**
             * Возвращает null, указывая на использование дефолтной конфигурации генерации от API.
             * @return null.
             */
            fun defaultGenerationConfig(): GenerationConfig? = null
        }
    }

    /**
     * Builder для создания списка SafetySetting.
     * Позволяет гибко и удобно настраивать параметры безопасности.
     */
    class SafetySettingsBuilder {
        private val safetySettingsList = mutableListOf<SafetySetting>()

        /**
         * Добавляет SafetySetting в список настроек.
         * @param setting Настройка безопасности.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun addSetting(setting: SafetySetting): SafetySettingsBuilder {
            safetySettingsList.add(setting)
            return this
        }

        /**
         * Устанавливает порог блокировки на BLOCK_NONE для указанной категории.
         * @param category Категория безопасности.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockNone(category: SafetyCategory): SafetySettingsBuilder =
            addSetting(SafetySetting(category = category.value, threshold = SafetyThreshold.BLOCK_NONE.value))

        /**
         * Устанавливает порог блокировки на BLOCK_ONLY_HIGH для указанной категории.
         * @param category Категория безопасности.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockOnlyHigh(category: SafetyCategory): SafetySettingsBuilder =
            addSetting(SafetySetting(category = category.value, threshold = SafetyThreshold.BLOCK_ONLY_HIGH.value))

        /**
         * Устанавливает порог блокировки на BLOCK_MEDIUM_AND_ABOVE для указанной категории.
         * @param category Категория безопасности.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockMediumAndAbove(category: SafetyCategory): SafetySettingsBuilder =
            addSetting(SafetySetting(category = category.value, threshold = SafetyThreshold.BLOCK_MEDIUM_AND_ABOVE.value))

        /**
         * Устанавливает порог блокировки на BLOCK_LOW_AND_ABOVE для указанной категории.
         * @param category Категория безопасности.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockLowAndAbove(category: SafetyCategory): SafetySettingsBuilder =
            addSetting(SafetySetting(category = category.value, threshold = SafetyThreshold.BLOCK_LOW_AND_ABOVE.value))

        /**
         * Устанавливает порог блокировки на BLOCK_MEDIUM_AND_ABOVE для категории HARM_CATEGORY_HARASSMENT.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockMediumAndAboveHarassment() = blockMediumAndAbove(SafetyCategory.HARM_CATEGORY_HARASSMENT)
        /**
         * Устанавливает порог блокировки на BLOCK_MEDIUM_AND_ABOVE для категории HARM_CATEGORY_HATE_SPEECH.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockMediumAndAboveHateSpeech() = blockMediumAndAbove(SafetyCategory.HARM_CATEGORY_HATE_SPEECH)
        /**
         * Устанавливает порог блокировки на BLOCK_MEDIUM_AND_ABOVE для категории HARM_CATEGORY_SEXUALLY_EXPLICIT.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockMediumAndAboveSexuallyExplicit() = blockMediumAndAbove(SafetyCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
        /**
         * Устанавливает порог блокировки на BLOCK_MEDIUM_AND_ABOVE для категории HARM_CATEGORY_DANGEROUS_CONTENT.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockMediumAndAboveDangerousContent() = blockMediumAndAbove(SafetyCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
        /**
         * Устанавливает порог блокировки на BLOCK_MEDIUM_AND_ABOVE для категории HARM_CATEGORY_CIVIC_INTEGRITY.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockMediumAndAboveCivicIntegrity() = blockMediumAndAbove(SafetyCategory.HARM_CATEGORY_CIVIC_INTEGRITY)

        /**
         * Устанавливает порог блокировки на BLOCK_ONLY_HIGH для категории HARM_CATEGORY_HARASSMENT.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockOnlyHighHarassment() = blockOnlyHigh(SafetyCategory.HARM_CATEGORY_HARASSMENT)
        /**
         * Устанавливает порог блокировки на BLOCK_ONLY_HIGH для категории HARM_CATEGORY_HATE_SPEECH.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockOnlyHighHateSpeech() = blockOnlyHigh(SafetyCategory.HARM_CATEGORY_HATE_SPEECH)
        /**
         * Устанавливает порог блокировки на BLOCK_ONLY_HIGH для категории HARM_CATEGORY_SEXUALLY_EXPLICIT.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockOnlyHighSexuallyExplicit() = blockOnlyHigh(SafetyCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
        /**
         * Устанавливает порог блокировки на BLOCK_ONLY_HIGH для категории HARM_CATEGORY_DANGEROUS_CONTENT.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockOnlyHighDangerousContent() = blockOnlyHigh(SafetyCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
        /**
         * Устанавливает порог блокировки на BLOCK_ONLY_HIGH для категории HARM_CATEGORY_CIVIC_INTEGRITY.
         * @return SafetySettingsBuilder для цепочки вызовов.
         */
        fun blockOnlyHighCivicIntegrity() = blockOnlyHigh(SafetyCategory.HARM_CATEGORY_CIVIC_INTEGRITY)


        /**
         * Завершает построение списка SafetySetting и возвращает его.
         * @return Список SafetySetting.
         */
        fun build(): List<SafetySetting> = safetySettingsList.toList()
    }


    /**
     * Builder для создания объекта GenerationConfig.
     * Позволяет гибко и удобно настраивать параметры генерации.
     */
    class GenerationConfigBuilder {
        private var temperature: Double? = null
        private var topP: Double? = null
        private var topK: Int? = null
        private var maxOutputTokens: Int? = null
        private var stopSequences: List<String>? = null

        /**
         * Устанавливает температуру генерации.
         * @param value Значение температуры (0.0 - 1.0).
         * @return GenerationConfigBuilder для цепочки вызовов.
         */
        fun temperature(value: Double?) = apply { this.temperature = value }
        /**
         * Устанавливает Top-P sampling.
         * @param value Значение Top-P (0.0 - 1.0).
         * @return GenerationConfigBuilder для цепочки вызовов.
         */
        fun topP(value: Double?) = apply { this.topP = value }
        /**
         * Устанавливает Top-K sampling.
         * @param value Значение Top-K (целое число).
         * @return GenerationConfigBuilder для цепочки вызовов.
         */
        fun topK(value: Int?) = apply { this.topK = value }
        /**
         * Устанавливает максимальное количество токенов в ответе.
         * @param value Максимальное количество токенов.
         * @return GenerationConfigBuilder для цепочки вызовов.
         */
        fun maxOutputTokens(value: Int?) = apply { this.maxOutputTokens = value }
        /**
         * Устанавливает стоп-последовательности. Генерация остановится, как только одна из последовательностей будет сгенерирована.
         * @param value Список стоп-последовательностей.
         * @return GenerationConfigBuilder для цепочки вызовов.
         */
        fun stopSequences(value: List<String>?) = apply { this.stopSequences = value }
        /**
         * Устанавливает стоп-последовательности (vararg версия).
         * @param value Переменное количество стоп-последовательностей.
         * @return GenerationConfigBuilder для цепочки вызовов.
         */
        fun stopSequences(vararg value: String) = apply { this.stopSequences = value.toList() }

        /**
         * Завершает построение объекта GenerationConfig и возвращает его.
         * @return GenerationConfig.
         */
        fun build(): GenerationConfig = GenerationConfig(
            temperature = temperature,
            topP = topP,
            topK = topK,
            maxOutputTokens = maxOutputTokens,
            stopSequences = stopSequences
        )
    }

    /**
     * Enum класс для выбора модели Gemini.
     * Добавьте сюда новые модели по мере их появления в API.
     */
    enum class GeminiModel(val modelName: String) {
        GEMINI_15PRO("gemini-1.5-pro"),
        GEMINI_20FLASH("gemini-2.0-flash"),
        GEMINI_20FLASH_EXP("gemini-2.0-flash-thinking-exp-01-21"),
        GEMINI_20FLASH_PRO("gemini-2.0-pro-exp-02-05"),
    }


}