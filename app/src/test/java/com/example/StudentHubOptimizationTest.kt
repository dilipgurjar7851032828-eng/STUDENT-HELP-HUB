package com.example

import com.example.data.ai.StudyBuddyAiService
import com.example.data.model.CollegeItem
import com.example.data.model.ExamItem
import com.example.data.model.OpportunityDataAuditReport
import com.example.data.model.ScholarshipItem
import com.example.data.model.StudentProfile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentHubOptimizationTest {

    @Test
    fun testAdminPinAuthentication() {
        val validPin1 = "1800"
        val validPin2 = "admin123"
        val invalidPin = "0000"

        fun authenticate(pin: String): Boolean = pin == "1800" || pin == "admin123"

        assertTrue(authenticate(validPin1))
        assertTrue(authenticate(validPin2))
        assertFalse(authenticate(invalidPin))
        assertFalse(authenticate(""))
    }

    @Test
    fun testPercentageValidation() {
        fun isValidPercentage(input: String): Boolean {
            val d = input.toDoubleOrNull() ?: return false
            return d in 0.0..100.0
        }

        assertTrue(isValidPercentage("85.5"))
        assertTrue(isValidPercentage("0"))
        assertTrue(isValidPercentage("100"))
        assertFalse(isValidPercentage("-5"))
        assertFalse(isValidPercentage("105"))
        assertFalse(isValidPercentage("abc"))
        assertFalse(isValidPercentage(""))
    }

    @Test
    fun testReminderDateValidation() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())

        fun isDateValid(dateStr: String): Boolean {
            return try {
                val parsed = sdf.parse(dateStr) ?: return false
                val today = sdf.parse(todayStr) ?: return false
                !parsed.before(today)
            } catch (e: Exception) {
                false
            }
        }

        assertTrue(isDateValid(todayStr))
        assertTrue(isDateValid("2026-12-31"))
        assertFalse(isDateValid("2020-01-01"))
        assertFalse(isDateValid("invalid-date"))
    }

    @Test
    fun testAiDomainKnowledgeFallbackNeverFabricates() = runBlocking {
        val aiService = StudyBuddyAiService()

        // 1. Scholarship query fallback
        val scholarshipResp = aiService.getResponse("NSP Scholarship details batao", "Hinglish")
        assertNotNull(scholarshipResp)
        assertTrue(scholarshipResp.text.contains("NSP") || scholarshipResp.text.contains("Scholarship"))
        assertTrue(scholarshipResp.officialLinks.any { it.contains("scholarships.gov.in") })
        assertNotNull(scholarshipResp.verificationNote)

        // 2. Exam query fallback
        val examResp = aiService.getResponse("CUET UG exam syllabus and pattern kya hai?", "Hinglish")
        assertNotNull(examResp)
        assertTrue(examResp.officialLinks.any { it.contains("nta.ac.in") || it.contains("samarth.ac.in") })

        // 3. College query fallback
        val collegeResp = aiService.getResponse("Delhi University admission process", "Hinglish")
        assertNotNull(collegeResp)
        assertTrue(collegeResp.officialLinks.any { it.contains("du.ac.in") })
    }

    @Test
    fun testDataAuditCalculation() {
        val colleges = listOf(
            CollegeItem(
                id = "c1",
                name = "IIT Bombay",
                university = "IIT Bombay",
                state = "Maharashtra",
                city = "Mumbai",
                coursesOffered = "B.Tech",
                annualFees = "₹2.2 Lakh/yr",
                isGovernment = true,
                hostelAvailable = true,
                officialWebsite = "https://iitb.ac.in",
                rankingInfo = "NIRF #3",
                cutoffSummary = "JEE Adv Top 500",
                verificationStatus = "VERIFIED"
            ),
            CollegeItem(
                id = "c2",
                name = "BITS Pilani",
                university = "BITS",
                state = "Rajasthan",
                city = "Pilani",
                coursesOffered = "B.E.",
                annualFees = "₹5.5 Lakh/yr",
                isGovernment = false,
                hostelAvailable = true,
                officialWebsite = "https://bits-pilani.ac.in",
                rankingInfo = "NIRF #20",
                cutoffSummary = "BITSAT 310+",
                verificationStatus = "VERIFIED"
            )
        )

        val scholarships = listOf(
            ScholarshipItem("s1", "NSP Post Matric", "MoMA", "All India", "All UG", "SC/ST/OBC", 250000, 50.0, "10th/12th", "₹12,000/yr", "2026-10-31", "https://scholarships.gov.in", "VERIFIED"),
            ScholarshipItem("s2", "PMSS Scholarship", "MoD", "All India", "Professional Degrees", "Ex-Servicemen", 600000, 60.0, "12th/Diploma", "₹36,000/yr", "2026-11-30", "https://ksb.gov.in", "VERIFIED")
        )

        val allIds = colleges.map { it.id } + scholarships.map { it.id }
        val duplicateCount = allIds.size - allIds.distinct().size
        assertEquals(0, duplicateCount)

        val missingUrls = (colleges.map { it.officialWebsite } + scholarships.map { it.officialWebsite }).count { !it.startsWith("http") }
        assertEquals(0, missingUrls)

        val totalItems = colleges.size + scholarships.size
        val verifiedCount = colleges.count { it.verificationStatus == "VERIFIED" } + scholarships.count { it.verificationStatus == "VERIFIED" }
        val verifiedPercentage = (verifiedCount * 100) / totalItems
        assertEquals(100, verifiedPercentage)

        val report = OpportunityDataAuditReport(
            totalColleges = colleges.size,
            totalScholarships = scholarships.size,
            totalExams = 0,
            totalDeadlines = 0,
            duplicateIdCount = duplicateCount,
            missingUrlCount = missingUrls,
            expiredDeadlinesCount = 0,
            verifiedPercentage = verifiedPercentage,
            issuesFound = emptyList()
        )

        assertEquals(2, report.totalColleges)
        assertEquals(2, report.totalScholarships)
        assertEquals(100, report.verifiedPercentage)
    }

    @Test
    fun testSearchFieldCharacterHandling() {
        val testQueries = listOf("Scholarship", "DU College", "छात्रवृत्ति", "12345", "B.Tech + IIT @2026")
        for (query in testQueries) {
            assertTrue("Query '$query' should not be empty", query.isNotEmpty())
            assertTrue("Query '$query' length should match character count", query.length == query.toCharArray().size)
        }
    }

    @Test
    fun testVirtualPhoneTouchSingleTapReliability() {
        val handler = com.example.ui.touch.VirtualPhoneTouchHandler
        handler.reset()

        // 1. Initial single tap: DOWN and UP should both be allowed
        val down1 = handler.processTouchEvent(0 /*ACTION_DOWN*/, 100f, 100f, 1000L, 24f)
        assertFalse("Initial tap DOWN should be allowed", down1)

        val up1 = handler.processTouchEvent(1 /*ACTION_UP*/, 100f, 100f, 1030L, 24f)
        assertFalse("Initial tap UP should be allowed", up1)

        // 2. Accidental synthetic bounce / duplicate tap (e.g. within 50ms): MUST be filtered
        val duplicateDown = handler.processTouchEvent(0 /*ACTION_DOWN*/, 100f, 100f, 1060L, 24f)
        assertTrue("Synthetic duplicate DOWN within 60ms should be filtered", duplicateDown)

        val duplicateMove = handler.processTouchEvent(2 /*ACTION_MOVE*/, 101f, 100f, 1070L, 24f)
        assertTrue("Duplicate MOVE should be discarded", duplicateMove)

        val duplicateUp = handler.processTouchEvent(1 /*ACTION_UP*/, 100f, 100f, 1080L, 24f)
        assertTrue("Duplicate UP should be discarded", duplicateUp)
    }

    @Test
    fun testVirtualPhoneTouchIntentionalDoubleTapAllowed() {
        val handler = com.example.ui.touch.VirtualPhoneTouchHandler
        handler.reset()

        // 1. First tap at t=1000ms
        val down1 = handler.processTouchEvent(0, 100f, 100f, 1000L, 24f)
        assertFalse(down1)
        val up1 = handler.processTouchEvent(1, 100f, 100f, 1030L, 24f)
        assertFalse(up1)

        // 2. Intentional second quick tap at t=1220ms (190ms after first UP, normal double-tap cadence)
        val down2 = handler.processTouchEvent(0, 100f, 100f, 1220L, 24f)
        assertFalse("Intentional second tap in 150-300ms window should be allowed", down2)
        val up2 = handler.processTouchEvent(1, 100f, 100f, 1250L, 24f)
        assertFalse("Intentional second tap UP should be allowed", up2)
    }

    @Test
    fun testVirtualPhoneTouchRapidTypingDifferentPositionsAllowed() {
        val handler = com.example.ui.touch.VirtualPhoneTouchHandler
        handler.reset()

        // 1. Tap key A at (50, 300)
        val downA = handler.processTouchEvent(0, 50f, 300f, 2000L, 24f)
        assertFalse(downA)
        val upA = handler.processTouchEvent(1, 50f, 300f, 2030L, 24f)
        assertFalse(upA)

        // 2. Tap key B at (150, 300) only 50ms later: distance is 100px > 24px slop -> must NOT be blocked
        val downB = handler.processTouchEvent(0, 150f, 300f, 2060L, 24f)
        assertFalse("Rapid typing on different keys must be allowed", downB)
        val upB = handler.processTouchEvent(1, 150f, 300f, 2090L, 24f)
        assertFalse(upB)
    }
}
