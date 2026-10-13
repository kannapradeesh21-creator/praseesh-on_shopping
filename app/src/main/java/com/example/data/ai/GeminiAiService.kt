package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateShoppingResponse(
        userMessage: String,
        catalog: List<ProductEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (isApiKeyValid(apiKey)) {
            val catalogSummary = catalog.take(8).joinToString("\n") {
                "- ${it.title} (${it.category}): Rs. ${it.price.toInt()} (Discount: ${it.discountPercent}% off, Stock: ${it.stockQuantity})"
            }

            val systemPrompt = """
                You are the friendly, expert AI Shopping Assistant for 'PRADEESH ONLINE SHOPPING' (Contact: 0759786878).
                Store policies:
                - Island-wide fast delivery.
                - Cash on Delivery (COD) and Bank Payment available.
                - Direct WhatsApp ordering to 0759786878.
                - Current featured catalog:
                $catalogSummary

                Help the customer find the best products, check deals, explain specifications, or guide them on ordering.
                Keep answers enthusiastic, concise, well-formatted with bullet points and emojis. Always mention they can order via WhatsApp at 0759786878.
            """.trimIndent()

            val prompt = "$systemPrompt\n\nCustomer question: $userMessage"
            val aiResponse = callGeminiApi(prompt, apiKey)
            if (aiResponse != null) return@withContext aiResponse
        }

        // Smart offline heuristic fallback
        generateOfflineAssistantResponse(userMessage, catalog)
    }

    suspend fun generateProductDescription(
        title: String,
        category: String,
        features: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (isApiKeyValid(apiKey)) {
            val prompt = """
                You are an e-commerce copywriting expert for PRADEESH ONLINE SHOPPING.
                Write an engaging, high-converting product description for:
                Title: $title
                Category: $category
                Key details: $features

                Format:
                1. A captivating 2-sentence hook highlighting value.
                2. 4 bullet points of standout benefits & specifications.
                3. A brief closing warranty & delivery guarantee.
                Keep it professional and ready to paste into the shop catalog.
            """.trimIndent()

            val aiResponse = callGeminiApi(prompt, apiKey)
            if (aiResponse != null) return@withContext aiResponse
        }

        // Heuristic fallback
        """
        Elevate your experience with the all-new $title, exclusively curated by Pradeesh Online Shopping. Crafted with premium grade materials to ensure unmatched reliability and satisfaction.

        ✨ Key Features:
        • High performance and modern ergonomic build
        • Verified quality assurance with manufacturer warranty
        • Fast dispatch and safe doorstep packaging
        • Specially discounted price for today's buyers

        📦 Cash on Delivery & WhatsApp support available at 0759786878.
        """.trimIndent()
    }

    suspend fun generateMarketingBannerIdea(
        theme: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (isApiKeyValid(apiKey)) {
            val prompt = """
                Create 3 catchy promotional marketing headlines and discount slogans for PRADEESH ONLINE SHOPPING for theme: '$theme'.
                Include emoji and discount call-to-action (e.g. Up to 50% OFF, Limited Stock).
            """.trimIndent()
            val aiResponse = callGeminiApi(prompt, apiKey)
            if (aiResponse != null) return@withContext aiResponse
        }

        """
        🔥 MEGA SALE ALERT: Up to 60% OFF on $theme!
        Limited time flash deals only at PRADEESH ONLINE SHOPPING.
        📞 Order now via WhatsApp: 0759786878
        """.trimIndent()
    }

    private fun isApiKeyValid(key: String?): Boolean {
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY" && key != "YOUR_API_KEY"
    }

    private fun callGeminiApi(prompt: String, apiKey: String): String? {
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseString = response.body?.string() ?: return null
                val rootJson = JSONObject(responseString)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text")
                    }
                }
            }
        } catch (_: Exception) {
            // Log or fallback
        }
        return null
    }

    private fun generateOfflineAssistantResponse(
        userMessage: String,
        catalog: List<ProductEntity>
    ): String {
        val lower = userMessage.lowercase()
        return when {
            "deal" in lower || "offer" in lower || "discount" in lower -> {
                val deals = catalog.filter { it.isDeal }.take(3)
                val dealList = deals.joinToString("\n") { "• ${it.title} - Now Rs. ${it.price.toInt()} (${it.discountPercent}% OFF!)" }
                "🔥 Here are our top hot deals today at Pradeesh Online Shopping:\n\n$dealList\n\nEnjoy island-wide doorstep delivery! Want to order directly? Chat with our hotline at 0759786878."
            }
            "contact" in lower || "phone" in lower || "call" in lower || "whatsapp" in lower -> {
                "📞 You can reach Pradeesh Online Shopping directly at:\n• Hotline / WhatsApp: 0759786878\n\nWe provide instant order confirmation, delivery tracking, and bank payment details!"
            }
            "payment" in lower || "pay" in lower || "cod" in lower -> {
                "💳 Payment Options at Pradeesh Online Shopping:\n1. Cash on Delivery (COD) - Pay securely when your package arrives at your door.\n2. Direct Bank Transfer - Transfer to our verified shop account.\n\nReady to place an order? Add items to your cart or order via WhatsApp at 0759786878!"
            }
            "delivery" in lower || "shipping" in lower || "time" in lower -> {
                "🚚 Delivery Information:\n• Island-wide delivery to all districts.\n• Colombo & suburbs: 1-2 working days.\n• Outstation: 2-4 working days.\n• All packages are bubble-wrapped and quality checked before dispatch!"
            }
            "electronic" in lower || "phone" in lower || "headphone" in lower || "watch" in lower -> {
                val electronics = catalog.filter { it.category == "Electronics" || it.category == "Watches & Accessories" }.take(3)
                val items = electronics.joinToString("\n") { "• ${it.title} - Rs. ${it.price.toInt()} [${it.stockQuantity} in stock]" }
                "⚡ Top Trending Gadgets in stock:\n\n$items\n\nAll electronics come with check warranty. Would you like to add one to your cart?"
            }
            "spice" in lower || "food" in lower || "grocery" in lower || "honey" in lower -> {
                "🌿 Our authentic Ceylon spices and pure forest honey are freshly packed with 100% natural aroma. Check out our 'Groceries & Spices' tab to see pure Ceylon cinnamon, cardamom, and organic bee honey!"
            }
            else -> {
                val randomPicks = catalog.shuffled().take(2)
                val picks = randomPicks.joinToString("\n") { "• ${it.title} (Rs. ${it.price.toInt()})" }
                "👋 Hello! Welcome to Pradeesh Online Shopping. I can help you find products, check prices, explore discounts, and guide your checkout.\n\nRecommended picks for you today:\n$picks\n\nFeel free to ask about any category, or contact our support team on WhatsApp at 0759786878!"
            }
        }
    }
}
