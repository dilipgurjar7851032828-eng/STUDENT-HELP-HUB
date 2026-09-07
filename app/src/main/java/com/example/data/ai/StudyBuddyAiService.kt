package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val officialLinks: List<String> = emptyList(),
    val verificationNote: String? = null
)

enum class MessageSender {
    USER,
    STUDY_BUDDY
}

class StudyBuddyAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    private val systemInstruction = """
        You are StudyBuddy AI, a personalized, caring educational advisor built for Indian students in the Student Help Hub app.
        Tagline: "Student ki padhai se career tak, sab ek jagah."
        Language support: Deep fluency in English, Hindi (Devanagari), and Hinglish (natural conversational mix of Hindi and English written in Latin script). Match the language and tone of the student naturally.
        
        CRITICAL PERSONALIZATION & HONESTY RULES:
        1. Context-Aware Guidance: Tailor all recommendations, eligibility advice, and next steps strictly around the student's personal profile (qualification, stream, category, state, marks, family income), their bookmarked/saved items, and their ongoing application tracker entries.
        2. Absolute Fact Honesty: NEVER fabricate or invent facts, unverified dates, cutoff percentiles, or institute fees.
        3. Mandatory Uncertainty Protocol: If official notifications for the current cycle are not yet finalized or if exact figures vary by quota/caste/institute, explicitly state that the information is uncertain/indicative and direct the student to verify on official government portals (e.g., scholarships.gov.in, nta.ac.in, josaa.nic.in, cuet.samarth.ac.in, sje.rajasthan.gov.in).
        4. Zero Guarantee Policy: NEVER promise or guarantee admission, selection rank, or scholarship disbursement.
        5. Proactive Next Steps: Mention required documentation (Aadhaar DBT seeding, current financial year income certificate, domicile, caste/EWS) and upcoming application milestones when relevant.
        6. Clean Formatting: Keep responses well-structured with clear bullet points, warm conversational empathy, and verified official portal links.
    """.trimIndent()

    suspend fun getResponse(
        userPrompt: String,
        context: StudentUserContext = StudentUserContext()
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        val contextSummary = context.buildContextSummary()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "Student Personal Context:\n$contextSummary\n\nStudent Query:\n$userPrompt")
                                })
                            })
                        })
                    })
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstruction)
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.3)
                        put("topK", 40)
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val respStr = response.body?.string().orEmpty()
                    val jsonResp = JSONObject(respStr)
                    val candidates = jsonResp.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val aiText = parts?.optJSONObject(0)?.optString("text")

                    if (!aiText.isNullOrBlank()) {
                        return@withContext ChatMessage(
                            sender = MessageSender.STUDY_BUDDY,
                            text = aiText,
                            officialLinks = listOf("https://scholarships.gov.in", "https://nta.ac.in", "https://ugc.gov.in"),
                            verificationNote = "⚠️ Authentic guidance based on your profile. Verified portals: scholarships.gov.in, nta.ac.in. Always cross-verify notices."
                        )
                    }
                }
            } catch (e: Exception) {
                // Graceful fallback to verified personalized offline rule engine
            }
        }

        // Offline / Rule-Based Personalized Expert Advisor Engine
        val fallbackResponse = generatePersonalizedDomainResponse(userPrompt, context)
        ChatMessage(
            sender = MessageSender.STUDY_BUDDY,
            text = fallbackResponse.text,
            officialLinks = fallbackResponse.links,
            verificationNote = "Verified Official Portals: " + fallbackResponse.links.joinToString(", ")
        )
    }

    // Overload for backward-compatibility if invoked with simple language string
    suspend fun getResponse(userPrompt: String, languagePreference: String): ChatMessage {
        return getResponse(userPrompt, StudentUserContext(languagePreference = languagePreference))
    }

    internal data class AiResult(val text: String, val links: List<String>)

    internal fun generatePersonalizedDomainResponse(query: String, context: StudentUserContext): AiResult {
        val q = query.lowercase().trim()
        val profile = context.profile
        val lang = context.languagePreference
        val isHindi = lang.equals("Hindi", ignoreCase = true) || q.contains("kya") || q.contains("kaise") || q.contains("hai") || q.contains("chahiye") || q.contains("batao")

        val studentName = profile?.fullName ?: "Student"
        val qual = profile?.qualification ?: "12th"
        val stream = profile?.stream ?: "General"
        val cat = profile?.category ?: "General"
        val state = profile?.state ?: "Delhi"
        val marks = profile?.marksPercentage ?: 75.0
        val income = profile?.annualIncomeRange ?: "₹2.5L - ₹8L"
        val prefCourse = profile?.preferredCourse ?: "Higher Education"

        // 1. Personal status / "mere liye" / recommendations query
        if (q.contains("mere liye") || q.contains("mere profile") || q.contains("recommend") || q.contains("my profile") || q.contains("eligible") || q.contains("kya available")) {
            val sb = StringBuilder()
            if (isHindi) {
                sb.append("Namaste **$studentName ji**! 🙏 Aapke profile ke aadhar par personalised analysis:\n\n")
                sb.append("📋 **Aapka Profile Context:**\n")
                sb.append("• **Yogyata:** $qual ($stream)\n")
                sb.append("• **Category & State:** $cat | Domicile: $state\n")
                sb.append("• **Marks & Aay:** $marks% | Family Income: $income\n")
                sb.append("• **Target Course:** $prefCourse\n\n")

                sb.append("🌟 **Aapke Liye Top Recommendations:**\n")
                if (stream.contains("Science", ignoreCase = true) || prefCourse.contains("Tech", ignoreCase = true) || prefCourse.contains("Engineering", ignoreCase = true)) {
                    sb.append("1. **JEE Main / State CET:** Engineering admissions ke liye. OBC/SC/ST/EWS certificates central format mein ready rakhein.\n")
                    sb.append("2. **NSP Central Sector Scholarship:** Class 12 board merit top 20th percentile (₹12,000/year graduation mein).\n")
                } else {
                    sb.append("1. **CUET (UG):** Central & State universities mein BA/B.Com/B.Sc ke subsidized courses ke liye.\n")
                    sb.append("2. **State Post-Matric Scholarship ($state):** Tuition fee reimbursement aur maintenance allowance.\n")
                }

                if (context.savedItems.isNotEmpty()) {
                    sb.append("\n🔖 **Aapke Saved Bookmarks (${context.savedItems.size}):**\n")
                    context.savedItems.take(3).forEach { item ->
                        sb.append("• [${item.itemType}] ${item.title} (${item.subtitle})\n")
                    }
                }

                if (context.applications.isNotEmpty()) {
                    sb.append("\n🚀 **Application Tracker Progress (${context.applications.size}):**\n")
                    context.applications.take(3).forEach { app ->
                        sb.append("• **${app.title}**: Status = `${app.status}`, Deadline: `${app.deadlineDate}`\n")
                    }
                }

                sb.append("\n⚠️ *Official Verification: Sabhi cutoffs aur dates verify karein official portals par. Ham kisi bhi seat ya grant ki guarantee nahi dete.*")
            } else {
                sb.append("Hello **$studentName**! Here is your personalized educational roadmap based on your profile:\n\n")
                sb.append("📋 **Your Profile Summary:**\n")
                sb.append("• **Qualification:** $qual in $stream\n")
                sb.append("• **Category & State:** $cat, Domicile: $state\n")
                sb.append("• **Academic Standing:** $marks% marks | Income: $income\n\n")

                sb.append("🎯 **Recommended Opportunities:**\n")
                sb.append("1. **NSP Central Sector Scheme:** Board merit scholarship (scholarships.gov.in).\n")
                sb.append("2. **State Domicile Concessions:** 85% home-state quota in $state state universities.\n")

                if (context.applications.isNotEmpty()) {
                    sb.append("\n📌 **Current Tracked Applications:**\n")
                    context.applications.take(3).forEach { app ->
                        sb.append("• ${app.title} (Status: ${app.status}, Target: ${app.deadlineDate})\n")
                    }
                }
                sb.append("\n⚠️ *Note: Cutoffs and seat matrices are subject to official counselling rounds. Always verify on official gateways.*")
            }
            return AiResult(sb.toString(), listOf("https://scholarships.gov.in", "https://nta.ac.in", "https://ugc.gov.in"))
        }

        // 2. Application tracker query
        if (q.contains("tracker") || q.contains("applied") || q.contains("meri application") || q.contains("track")) {
            val sb = StringBuilder()
            sb.append("📂 **Application Tracker Review for $studentName:**\n\n")
            if (context.applications.isEmpty()) {
                sb.append("Aapke tracker mein abhi koi application nahi hai.\n")
                sb.append("💡 *Tip: Aap kisi bhi Scholarship, College ya Exam card par 'Track App' click karke use yahan monitor kar sakte hain.*")
            } else {
                sb.append("Aapke paas कुल **${context.applications.size} applications** tracked hain:\n\n")
                context.applications.forEachIndexed { idx, app ->
                    val statusEmoji = when (app.status.uppercase()) {
                        "PLANNING" -> "📝"
                        "APPLIED" -> "📤"
                        "UNDER_REVIEW" -> "🔍"
                        "COMPLETED" -> "✅"
                        else -> "📌"
                    }
                    sb.append("${idx + 1}. $statusEmoji **${app.title}**\n")
                    sb.append("   • **Status:** `${app.status}` | **Target:** ${app.targetName}\n")
                    sb.append("   • **Deadline:** ${app.deadlineDate}\n")
                    if (app.portalLink.isNotBlank()) {
                        sb.append("   • **Official Portal:** ${app.portalLink}\n")
                    }
                }
                sb.append("\n⚠️ *Verification Notice: Check official institute websites regularly for round updates or merit list rollouts.*")
            }
            return AiResult(sb.toString(), listOf("https://scholarships.gov.in", "https://samarth.edu.in"))
        }

        // 3. Saved items / Bookmarks query
        if (q.contains("saved") || q.contains("bookmark") || q.contains("save kiya") || q.contains("meri list")) {
            val sb = StringBuilder()
            sb.append("🔖 **Aapke Saved Bookmarks ($studentName):**\n\n")
            if (context.savedItems.isEmpty()) {
                sb.append("Aapne abhi tak koi college ya scholarship save nahi kiya hai.\n")
                sb.append("💡 *Tip: Explore cards par Bookmark icon click karein taaki aap zaroori opportunities miss na karein.*")
            } else {
                sb.append("Aapne **${context.savedItems.size} opportunities** bookmarked ki hain:\n\n")
                context.savedItems.forEachIndexed { idx, item ->
                    sb.append("${idx + 1}. **${item.title}**\n")
                    sb.append("   • Type: `${item.itemType}` | Details: ${item.subtitle}\n")
                }
                sb.append("\n💡 *Aap inhein Application Tracker mein convert kar sakte hain aur 4-stage alerts set kar sakte hain.*")
            }
            return AiResult(sb.toString(), listOf("https://scholarships.gov.in", "https://collegedunia.com"))
        }

        // 4. Uncertainty & verification on cutoffs / exact dates / fees
        if (q.contains("cutoff") || q.contains("cut off") || q.contains("fees kitni") || q.contains("fee kitni") || q.contains("exact date") || q.contains("confirm")) {
            val text = """
            ⚠️ **Official Verification Notice (सत्यापन आवश्यक):**

            Admissions aur Scholarships ke cutoffs, fees aur dates har saal seats, candidate percentiles aur reservation quotas ke aadhar par vary karte hain:
            
            • **Cutoffs:** Kisi bhi saal ka exact cutoff pehle se predict nahi kiya ja sakta. Ye JoSAA, CSAS ya State Counselling Authority ki official rounds counselling par depend karta hai.
            • **Fees:** Govt colleges (₹4,000 - ₹25,000/yr) vs Private institutes (₹1,00,000 - ₹3,50,000/yr) alag hote hain, aur SC/ST/OBC/EWS fee concessions ke rules institute-specific hote hain.
            • **Action Required:** Kisi bhi agent ya unverified blog par vishwas na karein. Kripya official portal (`josaa.nic.in`, `admission.uod.ac.in`, `nta.ac.in`, `scholarships.gov.in`) ke latest circulars hi check karein.
            """.trimIndent()
            return AiResult(text, listOf("https://josaa.nic.in", "https://admission.uod.ac.in", "https://nta.ac.in", "https://scholarships.gov.in"))
        }

        // 5. State-specific (e.g. Rajasthan)
        if (q.contains("rajasthan") || state.equals("Rajasthan", ignoreCase = true) && (q.contains("scholarship") || q.contains("chhatravritti"))) {
            val text = """
            🏰 **Rajasthan State Guidance (Student: $studentName | Category: $cat):**

            1. **Rajasthan Uttar Matric Scholarship (SJE):**
               • **Eligibility:** Rajasthan Domicile; SC, ST, OBC, MBC, EWS students studying in accredited colleges.
               • **Aapke Profile ($cat):** If family income is within eligibility norms, 100% course fee reimbursement is available.
               • **Official Portal:** `sjmsnew.rajasthan.gov.in` (Apply via SSO ID).

            2. **Mukhyamantri Uccha Shiksha Chhatravritti Yojana:**
               • **Eligibility:** 60%+ in 12th Board (Aapka score: $marks%). Income < ₹2.5 Lakh.
               • **Amount:** ₹5,000/year. Portal: `hte.rajasthan.gov.in`.

            ⚠️ *Mandatory: Jan Aadhaar and Aadhaar-seeded bank account are strictly required for DBT transfer.*
            """.trimIndent()
            return AiResult(text, listOf("https://sjmsnew.rajasthan.gov.in", "https://hte.rajasthan.gov.in", "https://sje.rajasthan.gov.in"))
        }

        // 6. General Scholarship guidance personalized
        if (q.contains("scholarship") || q.contains("chhatravritti") || q.contains("paisa") || q.contains("financial") || q.contains("kitni milegi")) {
            val text = """
            🎓 **Personalized Scholarship Guidance for $studentName ($cat, $marks%):**

            1. **National Scholarship Portal (NSP):**
               • **Central Sector Scheme:** For College & University Students. Target: Top 20th percentile in 12th ($marks%).
               • **Post-Matric Scheme:** For $cat category with financial assistance covering tuition & maintenance.
               • **Portal:** `scholarships.gov.in` (Fee: ₹0).

            2. **Amount Breakdown:**
               • Graduation: ₹12,000/year (₹1,000/month).
               • Professional/Tech Courses: Full tuition waiver + maintenance (₹5,000 to ₹14,000/yr).

            3. **Essential Documents:**
               • Current financial year Income Certificate (issued by Tehsildar/SDM after April 1).
               • Domicile ($state) & Category Certificate ($cat).
               • Aadhaar seeded Bank Account for Direct Benefit Transfer (DBT).

            ⚠️ *Authenticity Notice: Scholarships are awarded strictly on verified merit and documentation. No agency can guarantee selection.*
            """.trimIndent()
            return AiResult(text, listOf("https://scholarships.gov.in", "https://aicte-india.org"))
        }

        // 7. Admissions / CUET / Colleges
        if (q.contains("cuet") || q.contains("du") || q.contains("college") || q.contains("admission") || q.contains("b.a") || q.contains("b.tech")) {
            val text = """
            🏛️ **College Admissions Guidance for $studentName ($stream):**

            • **Central Universities (DU, BHU, JNU, AU):**
              - Admission via **CUET (UG)** scores through university portals (DU CSAS: `admission.uod.ac.in`).
              - Target courses matched with your preference: $prefCourse.
            • **State Government Colleges ($state):**
              - 85% home state quota with subsidized fees (approx. ₹3,000 - ₹12,000/year).
            • **Required Verification:**
              - Check round-wise seat allocation schedules.

            ⚠️ *Note: Cutoffs vary significantly each round. Never pay any unverified middlemen or agents.*
            """.trimIndent()
            return AiResult(text, listOf("https://du.ac.in", "https://cuetug.ntaonline.in", "https://admission.uod.ac.in", "https://nta.ac.in"))
        }

        // 8. Default fallback
        val defaultText = """
        Namaste **$studentName ji**! 🙏 Main hoon **StudyBuddy AI**, aapka personal educational advisor.

        Aapke profile ke mutabiq ($qual, $stream, $cat, $state):
        • 🏛️ **College Finder:** Government vs Private colleges, fee concession rules.
        • 💰 **Scholarships:** NSP, State Schemes, and merit-cum-means grants.
        • 📝 **Entrance Exams:** CUET, JEE, NEET, state entrance criteria.
        • 📋 **Document Preparation:** Income, Domicile, Caste & EWS certificates.
        • 📂 **Tracker & Bookmarks:** Aapke saved items aur applications par advice.

        *Aap mujhse Hindi, English ya Hinglish mein kuch bhi pooch sakte hain!*
        """.trimIndent()
        return AiResult(defaultText, listOf("https://scholarships.gov.in", "https://nta.ac.in"))
    }
}
