package com.example.aiexamevaluator.repository

import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.data.model.GeminiRequest
import com.example.aiexamevaluator.data.model.Content
import com.example.aiexamevaluator.data.model.InlineData
import com.example.aiexamevaluator.data.model.Part
import com.example.aiexamevaluator.data.model.Question
import com.example.aiexamevaluator.data.remote.RetrofitInstance
import com.example.aiexamevaluator.utils.EncodedImage
import java.net.SocketTimeoutException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

class EvaluationRepository(
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val apiKey = "AIzaSyBsqJr2y22Ycj8kXNChXMnvI78fUlojVtQ"
    private val retryDelaysMs = listOf(2000L, 4000L, 8000L)

    suspend fun evaluateExam(
        questionImages: List<EncodedImage>,
        answerImages: List<EncodedImage>,
        name: String,
        roll: String,
        totalMarks: Int,
        isMcq: Boolean,
    ): Result<EvaluationResponse> {
        return try {
            val allImages = (questionImages + answerImages).take(10)
            if (allImages.isEmpty()) {
                return Result.failure(IllegalArgumentException("No images selected for evaluation."))
            }

            val prompt = buildPrompt(
                name = name,
                roll = roll,
                totalMarks = totalMarks,
                isMcq = isMcq,
                questionImageCount = questionImages.size,
                answerImageCount = answerImages.size,
            )

            val parts = mutableListOf<Part>()
            parts.add(Part(text = prompt))
            allImages
                .filter { it.base64Data.isNotBlank() }
                .forEach { base64Image ->
                    parts.add(
                        Part(
                            inline_data = InlineData(
                                mime_type = base64Image.mimeType.ifBlank { "image/jpeg" },
                                data = base64Image.base64Data,
                            ),
                        ),
                    )
                }

            if (parts.size == 1) {
                return Result.failure(IllegalArgumentException("No valid encoded images to evaluate."))
            }

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = parts,
                    ),
                ),
            )

            val rawResponse = callEvaluateWithRetry(request)

            val reply = rawResponse
                ?.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: return Result.failure(IllegalStateException("Invalid API response format"))

            val jsonText = extractJsonObject(reply)
                ?: return Result.failure(IllegalStateException("Model returned non-JSON content"))

            val parsed = parseEvaluationResponse(jsonText)
            if (parsed.questions.isEmpty()) {
                return Result.failure(IllegalStateException("Model response did not contain valid question evaluations."))
            }
            Result.success(parsed)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun callEvaluateWithRetry(request: GeminiRequest) =
        run {
            var lastException: Exception? = null
            val maxRetries = retryDelaysMs.size

            for (attempt in 0..maxRetries) {
                try {
                    return@run RetrofitInstance.api.evaluateExam(
                        apiKey = apiKey,
                        request = request,
                    )
                } catch (exception: Exception) {
                    lastException = exception

                    val isRetryable = shouldRetry(exception)
                    if (!isRetryable || attempt >= maxRetries) {
                        throw exception
                    }

                    delay(retryDelaysMs[attempt])
                }
            }

            throw lastException ?: IllegalStateException("Evaluation failed after retries")
        }

    private fun shouldRetry(exception: Exception): Boolean {
        if (exception is SocketTimeoutException) {
            return true
        }

        if (exception is HttpException) {
            return exception.code() == 429 || exception.code() == 503
        }

        return false
    }

    private fun buildPrompt(
        name: String,
        roll: String,
        totalMarks: Int,
        isMcq: Boolean,
        questionImageCount: Int,
        answerImageCount: Int,
    ): String {
        return """
You are a strict and fair exam evaluator.

You are given:
1. Question paper images
2. Student answer sheet images

---

STEP 1: Analyze Question Paper

- Carefully read the question paper
- Identify ALL questions and their numbers (e.g., Q2.1, Q2.2, etc.)
- Understand what each question requires

---

STEP 2: Analyze Answer Sheet

- Read all student answers carefully
- Match each answer with its corresponding question number

---

IMPORTANT RULES:

1. For ANY question present in the question paper but NOT found in the answer sheet:

- Mark as:
    "Not Attempted"
- marksObtained = 0

---

2. For attempted questions:

You MUST include:

- marksObtained
- maxMarks
- status = "Attempted"

- correctPoints:
    -> What is correct in the answer

- mistakes:
    -> What is wrong or missing

- deductionReason:
    -> Short explanation of why marks were deducted

---

3. DO NOT mix answers between questions

Each question must be evaluated independently.

---

4. If question numbering is unclear:

- Try to infer from content
- If not possible:
    - mark as unclear
    - reduce marks accordingly

---

OUTPUT FORMAT (STRICT JSON ONLY):

{
    "totalObtained": number,
    "confidenceScore": number,
    "overallFeedback": string,
    "questions": [
        {
            "questionNumber": string,
            "maxMarks": number,
            "marksObtained": number,
            "status": "Attempted" or "Not Attempted",
            "correctPoints": [string],
            "mistakes": [string],
            "deductionReason": string
        }
    ]
}

---

STRICT OUTPUT RULES:

- DO NOT skip any question
- DO NOT merge multiple questions
- DO NOT return explanation outside JSON
- DO NOT add extra fields

---

GOAL:

- Clear and structured evaluation
- Accurate marking
- Easy-to-understand feedback
""".trimIndent()
    }

    private fun extractJsonObject(text: String): String? {
        val trimmed = text.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed
        }

        val withoutFence = trimmed
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        if (withoutFence.startsWith("{") && withoutFence.endsWith("}")) {
            return withoutFence
        }

        val firstBrace = text.indexOf('{')
        val lastBrace = text.lastIndexOf('}')
        return if (firstBrace >= 0 && lastBrace > firstBrace) {
            text.substring(firstBrace, lastBrace + 1)
        } else {
            null
        }
    }

    private fun parseEvaluationResponse(jsonText: String): EvaluationResponse {
        val root = json.parseToJsonElement(jsonText).jsonObject

        val parsedQuestions = root["questions"]?.jsonArray
            ?.mapIndexedNotNull { index, element ->
                (element as? JsonObject)?.let { parseQuestion(it, index + 1) }
            }
            .orEmpty()

        val questions = parsedQuestions
            .distinctBy { it.questionNumber }
            .sortedBy { it.questionNumber }

        val computedTotal = questions.sumOf { it.marksObtained }
        val totalFromResponse = root.intFromAny("totalObtained")

        return EvaluationResponse(
            totalObtained = if (computedTotal > 0) computedTotal else totalFromResponse.coerceAtLeast(0),
            confidenceScore = root.intFromAny("confidenceScore").coerceIn(0, 100),
            overallFeedback = root.string("overallFeedback"),
            questions = questions,
        )
    }

    private fun parseQuestion(obj: JsonObject, fallbackNumber: Int): Question {
        val parsedQuestionNumber = obj.intFromAny("questionNumber")
            .takeIf { it > 0 }
            ?: extractNumberFromString(obj.string("questionNumber"))
            ?: fallbackNumber

        val maxMarks = obj.intFromAny("maxMarks").coerceAtLeast(0)
        val status = obj.string("status")
        val deductionReason = obj.string("deductionReason")
        val marks = when {
            status.equals("Not Attempted", ignoreCase = true) -> 0
            else -> obj.intFromAny("marksObtained").coerceIn(0, maxMarks)
        }

        return Question(
            questionNumber = parsedQuestionNumber,
            maxMarks = maxMarks,
            marksObtained = marks,
            correctPoints = obj.stringList("correctPoints"),
            mistakes = obj.stringList("mistakes"),
            suggestion = obj.string("suggestion").ifBlank { deductionReason },
            reasoning = obj.string("reasoning").ifBlank { status },
        )
    }

    private fun JsonObject.intFromAny(key: String): Int {
        val primitive = this[key]?.jsonPrimitive ?: return 0
        primitive.intOrNull?.let { return it }
        return primitive.safeContent()
            ?.let { extractNumberFromString(it) }
            ?: 0
    }

    private fun JsonObject.string(key: String): String {
        return this[key]?.jsonPrimitive?.safeContent().orEmpty()
    }

    private fun JsonObject.stringList(key: String): List<String> {
        val value = this[key] ?: return emptyList()
        if (value !is JsonArray) {
            return emptyList()
        }
        return value.mapNotNull { item ->
            (item as? JsonPrimitive)?.safeContent()
        }
    }

    private fun JsonPrimitive.safeContent(): String? {
        return runCatching { content }.getOrNull()
    }

    private fun extractNumberFromString(value: String): Int? {
        return Regex("\\d+").find(value)?.value?.toIntOrNull()
    }
}
