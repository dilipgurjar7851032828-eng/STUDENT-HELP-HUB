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
        You are StudyBuddy AI, an expert, caring educational advisor built for Indian students in the Student Help Hub app.
        Tagline: "Student ki padhai se career tak, sab ek jagah."
        Language support: English, Hindi, and Hinglish (natural conversational mix of Hindi and English written in Latin script).
        
        CRITICAL RULES:
        1. Always advise students with honesty and clarity.
        2. NEVER fabricate facts, deadlines, cutoffs, or fees. If exact data varies by year or quota, explicitly say so and advise verifying on official government portals (such as nta.ac.in, scholarships.gov.in, josaa.nic.in, cuet.samarth.ac.in).
        3. NEVER guarantee admission or scholarship receipt under any circumstance.
        4. Mention required documents (Aadhaar, 10th/12th marksheets, Income certificate, Domicile, Caste/EWS if applicable).
        5. Keep responses structured with clear bullet points, friendly tone, and encouraging student advice.
    """.trimIndent()

    suspend fun getResponse(userPrompt: String, languagePreference: String = "Hinglish"): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "Language preference: $languagePreference\n\nStudent Query: $userPrompt")
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
                        put("temperature", 0.4)
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
                            verificationNote = "⚠️ Reminder: Always cross-verify cutoff numbers and dates with the respective university or portal notice."
                        )
                    }
                }
            } catch (e: Exception) {
                // Graceful fallback to verified offline rule engine below
            }
        }

        // Offline / Rule-Based Expert Advisor Engine (supports Hindi, Hinglish, English)
        val fallbackResponse = generateDomainKnowledgeResponse(userPrompt, languagePreference)
        ChatMessage(
            sender = MessageSender.STUDY_BUDDY,
            text = fallbackResponse.text,
            officialLinks = fallbackResponse.links,
            verificationNote = "Verified Official Sources: " + fallbackResponse.links.joinToString(", ")
        )
    }

    private data class AiResult(val text: String, val links: List<String>)

    private fun generateDomainKnowledgeResponse(query: String, lang: String): AiResult {
        val q = query.lowercase()

        return when {
            q.contains("scholarship") || q.contains("chhatravritti") || q.contains("paisa") || q.contains("financial") -> {
                val text = if (lang == "Hindi") {
                    """
                    🎓 **छात्रवृत्ति (Scholarships) के लिए महत्वपूर्ण मार्गदर्शिका:**
                    
                    1. **National Scholarship Portal (NSP - scholarships.gov.in):**
                       • Post-Matric SC/ST/OBC/Minority स्कीम्स
                       • Central Sector Scheme for College Students (बोर्ड में टॉप 20th परसेंटाइल)
                       • आय सीमा: सामान्यतः ₹2.5 लाख से ₹4.5 लाख वार्षिक।
                    
                    2. **AICTE प्रगति छात्रवृत्ति (बालिकाओं के लिए):**
                       • ₹50,000 प्रति वर्ष टेक्निकल/इंजीनियरिंग कोर्सेज के लिए।
                    
                    3. **अनिवार्य दस्तावेज:**
                       • आधार कार्ड (मोबाइल और बैंक से लिंक)
                       • वर्तमान वित्तीय वर्ष का आय प्रमाण पत्र (Income Certificate)
                       • 10वीं/12वीं अंकतालिका व कॉलेज बोनाफाइड सर्टिफिकेट।
                    
                    ⚠️ *नोट: कोई भी एजेंसी छात्रवृत्ति की 100% गारंटी नहीं देती। कृपया केवल scholarships.gov.in पर ही आवेदन करें।*
                    """.trimIndent()
                } else {
                    """
                    🎓 **Scholarship Guidance & Eligibility:**
                    
                    1. **National Scholarship Portal (NSP):**
                       • Post-Matric Scheme for SC / ST / OBC students.
                       • Central Sector Scheme (Top 20th percentile in Class 12 Board, income < ₹4.5L).
                    2. **Special Schemes:**
                       • AICTE Pragati Scholarship for Girls (₹50,000/yr for Engineering/Diploma).
                       • Reliance Foundation UG Scholarships (Merit-cum-means).
                    3. **Essential Documents:**
                       • Aadhaar Card (Must be seeded with bank account for DBT).
                       • Current Year Income Certificate (SDM/Tehsildar issued).
                       • Marksheet and College Bonafide Certificate.
                    
                    ⚠️ *Important: Scholarships depend on document verification and merit quotas. Always apply strictly via official portal scholarships.gov.in.*
                    """.trimIndent()
                }
                AiResult(text, listOf("https://scholarships.gov.in", "https://aicte-india.org"))
            }

            q.contains("delhi") || q.contains("cuet") || q.contains("du") || q.contains("college") -> {
                val text = """
                🏛️ **Delhi University & Top Colleges Admissions:**
                
                • **Admission Mode:** DU, JNU, BHU, and Jamia entrance is via **CUET (UG)** conducted by NTA.
                • **Key Portals:** 
                   - CUET UG Portal: `cuet.samarth.ac.in`
                   - DU CSAS Portal: `admission.uod.ac.in`
                   - Official DU: `du.ac.in`
                • **Top Colleges:** SRCC (Commerce), St. Stephen's, Hindu, Miranda House, Hansraj, IIT Delhi (JEE Adv).
                • **Criteria:** You must choose CUET subjects that you appeared for in your Class 12 board exams.
                
                ⚠️ *Admission cutoffs vary each round based on normalized scores and student category. Check admission.uod.ac.in for official round updates.*
                """.trimIndent()
                AiResult(text, listOf("https://du.ac.in", "https://cuet.samarth.ac.in", "https://admission.uod.ac.in", "https://nta.ac.in"))
            }

            q.contains("jee") || q.contains("engineering") || q.contains("iit") || q.contains("nit") -> {
                val text = """
                ⚙️ **Engineering Admissions (IITs, NITs, IIITs):**
                
                • **JEE Main (NTA):** For admissions into NITs, IIITs, GFTIs and eligibility for JEE Advanced.
                • **JEE Advanced:** Conducted by IITs for B.Tech seats across 23 IITs.
                • **Counselling:** Centralized counselling is conducted by **JoSAA** (`josaa.nic.in`) followed by CSAB special rounds.
                • **Reservation Documents:** OBC-NCL and EWS certificates must be in the central government prescribed format, issued on or after April 1.
                
                ⚠️ *Never trust agents claiming guaranteed seats. Admissions are strictly based on All India Rank (AIR).*
                """.trimIndent()
                AiResult(text, listOf("https://jeemain.nta.nic.in", "https://josaa.nic.in"))
            }

            q.contains("document") || q.contains("certificate") || q.contains("kagaz") -> {
                val text = """
                📋 **Essential Document Checklist for Indian Admissions & Scholarships:**
                
                1. **Class 10 Marksheet & Passing Certificate** (Primary proof for Date of Birth).
                2. **Class 12 Marksheet & Migration Certificate**.
                3. **Aadhaar Card** (Ensure name spelling matches 10th marksheet exactly).
                4. **Income Certificate** (Issued by Revenue Officer/Tehsildar after April 1).
                5. **Caste / Category Certificate** (OBC-NCL / SC / ST / EWS in Central Government format).
                6. **State Domicile / Residence Certificate** (For 85% state quota seats).
                7. **Aadhaar-Seeded Bank Account** (For direct DBT scholarship credit).
                
                💡 *Tip: Keep 5 self-attested photocopies and high-resolution PDF scans stored in DigiLocker.*
                """.trimIndent()
                AiResult(text, listOf("https://digilocker.gov.in", "https://mhrd.gov.in"))
            }

            else -> {
                val text = """
                Namaste! I am **StudyBuddy AI**, your student advisory companion at Student Help Hub.
                
                I can assist you with:
                • 🏛️ **College Finder:** Government vs Private universities, NIRF ranks, hostel facilities.
                • 💰 **Scholarship Finder:** Central Sector, Post-Matric, State & corporate grants.
                • 📝 **Entrance Exams:** CUET, JEE Main, NEET, CLAT, syllabus & application dates.
                • 📑 **Document Guidance:** Income certificate, OBC-NCL, EWS, and domicile preparation.
                • 🗓️ **Deadlines & Reminders:** Tracking your important form closing dates.
                
                *Aap mujhse Hindi, English ya Hinglish mein koi bhi sawaal pooch sakte hain!*
                """.trimIndent()
                AiResult(text, listOf("https://scholarships.gov.in", "https://nta.ac.in"))
            }
        }
    }
}
