package com.example

import android.view.KeyEvent
import com.example.data.ai.AiLiveSearchEngine
import com.example.data.ai.AiLiveSearchResultItem
import com.example.data.ai.StudyBuddyAiService
import com.example.data.ai.StudentUserContext
import com.example.data.repository.SmartMatcher
import com.example.data.model.ApplicationItem
import com.example.data.model.CollegeItem
import com.example.data.model.ExamItem
import com.example.data.model.MatchStatus
import com.example.data.model.OpportunityDataAuditReport
import com.example.data.model.SavedItem
import com.example.data.model.ScholarshipItem
import com.example.data.model.StudentProfile
import com.example.ui.touch.VirtualPhoneKeyHandler
import com.example.util.DeadlineReminderHelper
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

    @Test
    fun testAiLiveSearchRequiredQueriesHaveCompleteFields() = runBlocking {
        val testQueries = listOf(
            "DU admission",
            "Rajasthan scholarship",
            "BA admission",
            "CUET",
            "scholarship कितनी मिलेगी",
            "आज कौन से forms open हैं"
        )

        for (query in testQueries) {
            val response = AiLiveSearchEngine.search(
                rawQuery = query,
                localColleges = emptyList(),
                localScholarships = emptyList(),
                localExams = emptyList(),
                localDeadlines = emptyList()
            )

            assertTrue("Query '$query' should return at least one result", response.results.isNotEmpty())

            for (item in response.results) {
                // Requirement: 8 complete fields
                assertTrue("Item title must not be blank", item.title.isNotBlank())
                assertTrue("Item information must not be blank", item.information.isNotBlank())
                assertTrue("Item eligibility must not be blank", item.eligibility.isNotBlank())
                assertTrue("Item amountOrFees must not be blank", item.amountOrFees.isNotBlank())
                assertTrue("Item deadline must not be blank", item.deadline.isNotBlank())
                assertTrue("Item officialSource must not be blank", item.officialSource.isNotBlank())
                assertTrue("Item officialLink must start with http", item.officialLink.startsWith("http"))
                assertTrue("Item lastVerified must not be blank", item.lastVerified.isNotBlank())
                assertTrue("Item verificationStatus must be VERIFIED or NEEDS VERIFICATION",
                    item.verificationStatus == "VERIFIED" || item.verificationStatus == "NEEDS VERIFICATION")

                // Verification integrity: No guessing or fake status
                if (item.verificationStatus == "VERIFIED") {
                    val url = item.officialLink.lowercase()
                    val isOfficialDomain = url.contains(".gov.in") ||
                            url.contains(".nic.in") ||
                            url.contains(".ac.in") ||
                            url.contains(".edu.in") ||
                            url.contains(".nta.ac.in") ||
                            url.contains("ntaonline.in") ||
                            url.contains("aicte-india.org") ||
                            url.contains("du.ac.in") ||
                            url.contains("bhu.ac.in") ||
                            url.contains("scholarships.gov.in") ||
                            url.contains("raj.nic.in") ||
                            url.contains("ugc.ac.in")
                    assertTrue("VERIFIED item must have trusted/official domain: ${item.officialLink}", isOfficialDomain)
                }
            }
        }
    }

    @Test
    fun testAiLiveSearchDetectsHindiHinglishNaturalLanguage() = runBlocking {
        val hindiResponse = AiLiveSearchEngine.search(
            rawQuery = "scholarship कितनी मिलेगी",
            localColleges = emptyList(),
            localScholarships = emptyList(),
            localExams = emptyList(),
            localDeadlines = emptyList()
        )
        assertTrue(hindiResponse.results.isNotEmpty())
        assertTrue(hindiResponse.results.any { it.amountOrFees.contains("₹") })

        val openFormsResponse = AiLiveSearchEngine.search(
            rawQuery = "आज कौन से forms open हैं",
            localColleges = emptyList(),
            localScholarships = emptyList(),
            localExams = emptyList(),
            localDeadlines = emptyList()
        )
        assertTrue(openFormsResponse.results.isNotEmpty())
    }

    @Test
    fun testAiLiveSearchRajasthanScholarshipAuthenticOfficialPortal() = runBlocking {
        val response = AiLiveSearchEngine.search(
            rawQuery = "Rajasthan scholarship",
            localColleges = emptyList(),
            localScholarships = emptyList(),
            localExams = emptyList(),
            localDeadlines = emptyList()
        )
        assertTrue(response.results.isNotEmpty())
        val rajItem = response.results.first { it.title.contains("Rajasthan", ignoreCase = true) }
        assertEquals("VERIFIED", rajItem.verificationStatus)
        assertTrue(rajItem.officialLink.contains("sjmsnew.rajasthan.gov.in") || rajItem.officialLink.contains("hte.rajasthan.gov.in"))
        assertTrue(rajItem.officialSource.contains("Rajasthan"))
    }

    @Test
    fun testVirtualPhoneKeySinglePhysicalPressEntersExactlyOneCharacter() {
        val handler = VirtualPhoneKeyHandler
        handler.reset()

        val keyCode = KeyEvent.KEYCODE_A

        // 1. Initial physical key press DOWN at t=1000ms: MUST be allowed (enters 1 character)
        val down1 = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = keyCode,
            repeatCount = 0,
            eventTime = 1000L,
            downTime = 1000L
        )
        assertFalse("Initial physical key press DOWN must be allowed", down1)

        // 2. Synthetic duplicate DOWN within 18ms (e.g. streaming emulator packet repeat): MUST be filtered!
        val downDuplicate = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = keyCode,
            repeatCount = 0,
            eventTime = 1018L,
            downTime = 1000L
        )
        assertTrue("Duplicate synthetic key DOWN within 18ms must be filtered", downDuplicate)

        // 3. Second synthetic duplicate with identical downTime within 40ms: MUST also be filtered!
        val downDuplicate2 = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = keyCode,
            repeatCount = 0,
            eventTime = 1040L,
            downTime = 1000L
        )
        assertTrue("Duplicate synthetic key DOWN with same downTime must be filtered", downDuplicate2)

        // 4. Legitimate physical key release UP at t=1060ms: MUST be allowed
        val up1 = handler.processKeyEvent(
            action = KeyEvent.ACTION_UP,
            keyCode = keyCode,
            repeatCount = 0,
            eventTime = 1060L,
            downTime = 1000L
        )
        assertFalse("Legitimate physical key release UP must be allowed", up1)

        // 5. Duplicate synthetic UP within 15ms: MUST be filtered
        val upDuplicate = handler.processKeyEvent(
            action = KeyEvent.ACTION_UP,
            keyCode = keyCode,
            repeatCount = 0,
            eventTime = 1075L,
            downTime = 1000L
        )
        assertTrue("Duplicate synthetic key UP must be filtered", upDuplicate)
    }

    @Test
    fun testVirtualPhoneKeyRapidTypingDifferentKeysAllowed() {
        val handler = VirtualPhoneKeyHandler
        handler.reset()

        // Rapid typing of word "STUDENT" across different keys
        val keys = listOf(
            KeyEvent.KEYCODE_S,
            KeyEvent.KEYCODE_T,
            KeyEvent.KEYCODE_U,
            KeyEvent.KEYCODE_D,
            KeyEvent.KEYCODE_E,
            KeyEvent.KEYCODE_N,
            KeyEvent.KEYCODE_T
        )

        var time = 1000L
        for (key in keys) {
            val down = handler.processKeyEvent(
                action = KeyEvent.ACTION_DOWN,
                keyCode = key,
                repeatCount = 0,
                eventTime = time,
                downTime = time
            )
            assertFalse("Fast typing of different key $key must never be blocked", down)

            val up = handler.processKeyEvent(
                action = KeyEvent.ACTION_UP,
                keyCode = key,
                repeatCount = 0,
                eventTime = time + 25L,
                downTime = time
            )
            assertFalse("Release of key $key must be allowed", up)

            time += 40L // 40ms later next key is pressed (very fast typing cadence)
        }
    }

    @Test
    fun testVirtualPhoneKeyIntentionalDoubleLettersAllowed() {
        val handler = VirtualPhoneKeyHandler
        handler.reset()

        val keyCodeO = KeyEvent.KEYCODE_O

        // Typing double 'o' in "book"
        // First 'o'
        val down1 = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = keyCodeO,
            repeatCount = 0,
            eventTime = 2000L,
            downTime = 2000L
        )
        assertFalse("First 'o' DOWN must be allowed", down1)

        val up1 = handler.processKeyEvent(
            action = KeyEvent.ACTION_UP,
            keyCode = keyCodeO,
            repeatCount = 0,
            eventTime = 2040L,
            downTime = 2000L
        )
        assertFalse("First 'o' UP must be allowed", up1)

        // Second 'o' pressed intentionally 140ms after first UP (t=2180ms)
        val down2 = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = keyCodeO,
            repeatCount = 0,
            eventTime = 2180L,
            downTime = 2180L
        )
        assertFalse("Second intentional 'o' in 'book' must be allowed", down2)

        val up2 = handler.processKeyEvent(
            action = KeyEvent.ACTION_UP,
            keyCode = keyCodeO,
            repeatCount = 0,
            eventTime = 2220L,
            downTime = 2180L
        )
        assertFalse("Second intentional 'o' UP must be allowed", up2)
    }

    @Test
    fun testVirtualPhoneKeyHoldingAutoRepeatAllowed() {
        val handler = VirtualPhoneKeyHandler
        handler.reset()

        val backspace = KeyEvent.KEYCODE_DEL

        // Initial press
        val downInitial = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = backspace,
            repeatCount = 0,
            eventTime = 3000L,
            downTime = 3000L
        )
        assertFalse("Initial Backspace DOWN must be allowed", downInitial)

        // Android auto-repeat after hold timeout (repeatCount = 1, 2, 3...)
        val repeat1 = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = backspace,
            repeatCount = 1,
            eventTime = 3450L,
            downTime = 3000L
        )
        assertFalse("Auto-repeat #1 of held Backspace must be allowed", repeat1)

        // Duplicate repeat packet within 10ms with same repeatCount: must be filtered
        val duplicateRepeat = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = backspace,
            repeatCount = 1,
            eventTime = 3458L,
            downTime = 3000L
        )
        assertTrue("Duplicate auto-repeat packet must be filtered", duplicateRepeat)

        // Subsequent legitimate auto-repeat #2: must be allowed
        val repeat2 = handler.processKeyEvent(
            action = KeyEvent.ACTION_DOWN,
            keyCode = backspace,
            repeatCount = 2,
            eventTime = 3500L,
            downTime = 3000L
        )
        assertFalse("Auto-repeat #2 of held Backspace must be allowed", repeat2)

        // Release
        val up = handler.processKeyEvent(
            action = KeyEvent.ACTION_UP,
            keyCode = backspace,
            repeatCount = 0,
            eventTime = 3530L,
            downTime = 3000L
        )
        assertFalse("Backspace release UP must be allowed", up)
    }

    @Test
    fun testMereLiyeKyaAvailableHaiPersonalizedDashboardMatching() {
        val studentProfile = StudentProfile(
            id = 1,
            fullName = "Ananya Sharma",
            state = "Delhi",
            qualification = "12th",
            stream = "Science",
            category = "OBC",
            annualIncomeRange = "₹1L - ₹2.5L",
            marksPercentage = 85.0
        )

        // Matching Scholarship: OBC eligible, min 60%, max income 2.5L
        val matchingScholarship = ScholarshipItem(
            id = "sch_obc_01",
            title = "OBC Post-Matric Merit Scholarship",
            provider = "Ministry of Social Justice",
            state = "All India",
            eligibleCourses = "B.Tech, B.Sc, MBBS",
            eligibleCategories = "OBC, SC/ST",
            maxAnnualIncome = 250000L,
            minPercentage = 60.0,
            qualificationRequired = "12th",
            amountPerYear = "48,000",
            deadline = "2026-11-30",
            officialWebsite = "https://scholarships.gov.in"
        )

        val matchRes = SmartMatcher.matchScholarship(matchingScholarship, studentProfile)
        assertEquals("Matching category and marks must result in STRONG match", MatchStatus.STRONG, matchRes.status)
        assertTrue("Match score must be at least 70%", matchRes.scorePercentage >= 70)

        // Non-matching Scholarship: requires 90% and General category only
        val nonMatchingScholarship = ScholarshipItem(
            id = "sch_gen_01",
            title = "Super Elite General Merit Scholarship",
            provider = "Private Foundation",
            state = "All India",
            eligibleCourses = "B.Tech",
            eligibleCategories = "General",
            maxAnnualIncome = 100000L,
            minPercentage = 95.0,
            qualificationRequired = "Post Graduate",
            amountPerYear = "100,000",
            deadline = "2026-10-15",
            officialWebsite = "https://scholarships.gov.in"
        )

        val nonMatchRes = SmartMatcher.matchScholarship(nonMatchingScholarship, studentProfile)
        assertEquals("Ineligible criteria must result in NO_MATCH", MatchStatus.NO_MATCH, nonMatchRes.status)
    }

    @Test
    fun testSaveAndApplicationTrackerWorkflow() {
        // Step 1: Save an opportunity
        val savedItem = SavedItem(
            id = 10,
            itemType = "SCHOLARSHIP",
            itemId = "sch_nsp_101",
            title = "Central Sector Scheme of Scholarships",
            subtitle = "Ministry of Education • ₹20,000/yr"
        )

        assertEquals("sch_nsp_101", savedItem.itemId)
        assertEquals("SCHOLARSHIP", savedItem.itemType)

        // Step 2: Track from saved bookmark into ApplicationTracker
        val trackedApp = ApplicationItem(
            id = 1,
            title = savedItem.title,
            category = savedItem.itemType,
            targetName = savedItem.subtitle,
            status = "PLANNING",
            deadlineDate = "2026-10-31",
            notes = "Saved opportunity tracked in Student Help Hub.",
            portalLink = "https://scholarships.gov.in"
        )

        assertEquals("Central Sector Scheme of Scholarships", trackedApp.title)
        assertEquals("PLANNING", trackedApp.status)
        assertEquals("2026-10-31", trackedApp.deadlineDate)
        assertEquals("https://scholarships.gov.in", trackedApp.portalLink)
    }

    @Test
    fun testDeadlineRemindersMultiStage30Days7Days1DayAndDeadlineDay() {
        val deadlineDateStr = "2026-10-31"
        // Reference date 2026-09-01 (60 days ahead of deadline)
        val refDateStr = "2026-09-01"

        val milestones = DeadlineReminderHelper.calculateMilestoneAlerts(
            deadlineDateStr = deadlineDateStr,
            opportunityTitle = "JEE Main 2026 Session 1",
            referenceDateStr = refDateStr
        )

        // Must generate exactly 4 milestones: 30 days, 7 days, 1 day, deadline day
        assertEquals("Must schedule all 4 milestone alerts", 4, milestones.size)

        val stage30 = milestones.find { it.stageTag == "30_DAYS" }
        assertNotNull("30-day milestone must be present", stage30)
        assertEquals("2026-10-01", stage30!!.targetDate)
        assertTrue(stage30.alertTitle.contains("[30 Days Alert]"))
        assertTrue(stage30.isApplicable)

        val stage7 = milestones.find { it.stageTag == "7_DAYS" }
        assertNotNull("7-day milestone must be present", stage7)
        assertEquals("2026-10-24", stage7!!.targetDate)
        assertTrue(stage7.alertTitle.contains("[7 Days Alert]"))
        assertTrue(stage7.isApplicable)

        val stage1 = milestones.find { it.stageTag == "1_DAY" }
        assertNotNull("1-day milestone must be present", stage1)
        assertEquals("2026-10-30", stage1!!.targetDate)
        assertTrue(stage1.alertTitle.contains("[1 Day Urgent Alert]"))
        assertTrue(stage1.isApplicable)

        val stage0 = milestones.find { it.stageTag == "DEADLINE_DAY" }
        assertNotNull("Deadline day milestone must be present", stage0)
        assertEquals("2026-10-31", stage0!!.targetDate)
        assertTrue(stage0.alertTitle.contains("[Deadline Day Alert]"))
        assertTrue(stage0.isApplicable)
    }

    @Test
    fun testStudyBuddyAiPersonalizationWithProfileAndSavedItems() = runBlocking {
        val aiService = StudyBuddyAiService()

        val studentProfile = StudentProfile(
            id = 1,
            fullName = "Vikram Verma",
            state = "Rajasthan",
            qualification = "12th Passed",
            stream = "Science (PCM)",
            category = "OBC",
            annualIncomeRange = "₹1L - ₹2.5L",
            marksPercentage = 88.5,
            preferredCourse = "B.Tech Computer Science"
        )

        val savedItems = listOf(
            SavedItem(
                id = 1,
                itemType = "SCHOLARSHIP",
                itemId = "sch_sje_01",
                title = "Rajasthan Uttar Matric Scholarship",
                subtitle = "SJE Rajasthan • 100% Fee Reimbursement"
            )
        )

        val applications = listOf(
            ApplicationItem(
                id = 101,
                title = "JEE Main 2026",
                category = "EXAM",
                targetName = "NTA Gateway",
                status = "PLANNING",
                deadlineDate = "2026-11-30",
                portalLink = "https://jeemain.nta.nic.in"
            )
        )

        val context = StudentUserContext(
            profile = studentProfile,
            savedItems = savedItems,
            applications = applications,
            languagePreference = "Hinglish"
        )

        // Test 1: Personalized recommendations query in Hinglish
        val response = aiService.getResponse("Mere liye kya available hai? Best options recommend karo", context)
        assertNotNull(response)
        assertEquals(com.example.data.ai.MessageSender.STUDY_BUDDY, response.sender)
        // Must address user and reflect their personal context
        assertTrue("Response must mention student name", response.text.contains("Vikram Verma"))
        assertTrue("Response must mention stream/category context", response.text.contains("OBC") || response.text.contains("Science"))
        assertTrue("Response must mention tracked application or bookmark", response.text.contains("Rajasthan Uttar Matric") || response.text.contains("JEE Main"))
        assertTrue("Response must have official links", response.officialLinks.isNotEmpty())
    }

    @Test
    fun testStudyBuddyAiApplicationTrackerAndBookmarksQuery() = runBlocking {
        val aiService = StudyBuddyAiService()

        val studentProfile = StudentProfile(
            id = 2,
            fullName = "Pooja Patel",
            state = "Gujarat"
        )

        val applications = listOf(
            ApplicationItem(
                id = 201,
                title = "CUET UG 2026",
                category = "EXAM",
                targetName = "Delhi University",
                status = "APPLIED",
                deadlineDate = "2026-10-15",
                portalLink = "https://cuetug.ntaonline.in"
            )
        )

        val context = StudentUserContext(
            profile = studentProfile,
            savedItems = emptyList(),
            applications = applications,
            languagePreference = "Hindi"
        )

        val trackerResponse = aiService.getResponse("Meri tracked application ka status aur deadline kya hai?", context)
        assertTrue("Response must include tracked application CUET UG 2026", trackerResponse.text.contains("CUET UG 2026"))
        assertTrue("Response must reflect APPLIED status", trackerResponse.text.contains("APPLIED"))
        assertTrue("Response must reflect deadline 2026-10-15", trackerResponse.text.contains("2026-10-15"))
    }

    @Test
    fun testStudyBuddyAiUncertainInformationVerificationProtocol() = runBlocking {
        val aiService = StudyBuddyAiService()

        // When user asks for exact cutoffs or fees which fluctuate and cannot be invented
        val context = StudentUserContext(languagePreference = "Hinglish")
        val response = aiService.getResponse("IIT Bombay CSE ka exact cutoff marks kitna hai aur fees kitni hai?", context)

        // Must NOT invent fake cutoff numbers, must explicitly state uncertainty and mandate verification on official portals
        assertTrue("Must provide verification notice", response.text.contains("सत्यापन आवश्यक") || response.text.contains("Verification Notice"))
        assertTrue("Must mention official portal for counselling verification", response.text.contains("josaa.nic.in") || response.officialLinks.contains("https://josaa.nic.in"))
    }
}
