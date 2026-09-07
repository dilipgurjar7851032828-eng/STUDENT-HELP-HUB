package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Rohan Sharma",
    val email: String = "rohan.student@example.com",
    val state: String = "Delhi",
    val qualification: String = "12th Standard",
    val stream: String = "Science (PCM)",
    val category: String = "General",
    val annualIncomeRange: String = "₹2.5L - ₹8L",
    val marksPercentage: Double = 82.5,
    val preferredCourse: String = "Engineering & Technology",
    val isHostelNeeded: Boolean = true,
    val isGuest: Boolean = false,
    val referralCode: String = "SHUB-DEL782",
    val referralCount: Int = 3,
    val badgesUnlocked: String = "PROFILE_COMPLETED,DOCS_READY,OPPORTUNITY_SAVER",
    val notificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val selectedLanguage: String = "Hinglish" // "English", "Hindi", "Hinglish"
)

@Entity(tableName = "colleges")
data class CollegeItem(
    @PrimaryKey val id: String,
    val name: String,
    val university: String,
    val state: String,
    val city: String,
    val coursesOffered: String, // Comma-separated: "B.Tech, M.Tech, Ph.D"
    val annualFees: String,
    val isGovernment: Boolean,
    val hostelAvailable: Boolean,
    val officialWebsite: String,
    val rankingInfo: String,
    val cutoffSummary: String,
    val verificationStatus: String = "VERIFIED", // VERIFIED, NEEDS VERIFICATION, EXPIRED
    val lastUpdated: String = "2026-08-15",
    val viewCount: Int = 1250,
    val admissionDeadline: String = "2026-09-30"
) {
    val officialSource: String get() = university
    val applicationLink: String get() = officialWebsite
}

@Entity(tableName = "scholarships")
data class ScholarshipItem(
    @PrimaryKey val id: String,
    val title: String,
    val provider: String,
    val state: String, // "All India" or state
    val eligibleCourses: String,
    val eligibleCategories: String, // "All", "General", "OBC", "SC/ST", "EWS", "Girls"
    val maxAnnualIncome: Long, // e.g. 250000, 800000
    val minPercentage: Double, // e.g. 60.0
    val qualificationRequired: String,
    val amountPerYear: String,
    val deadline: String,
    val officialWebsite: String,
    val verificationStatus: String = "VERIFIED",
    val lastUpdated: String = "2026-08-20",
    val viewCount: Int = 3420
) {
    val officialSource: String get() = provider
    val applicationLink: String get() = officialWebsite
}

@Entity(tableName = "exams")
data class ExamItem(
    @PrimaryKey val id: String,
    val title: String,
    val conductingBody: String,
    val eligibility: String,
    val coursesTargeted: String,
    val examDate: String,
    val applicationDeadline: String,
    val officialWebsite: String,
    val syllabusSummary: String,
    val verificationStatus: String = "VERIFIED",
    val lastUpdated: String = "2026-08-10",
    val viewCount: Int = 4100,
    val applicationFee: String = "₹1,000 (Gen) / ₹900 (OBC) / ₹500 (SC/ST/PwD)"
) {
    val officialSource: String get() = conductingBody
    val applicationLink: String get() = officialWebsite
}

@Entity(tableName = "deadlines")
data class DeadlineItem(
    @PrimaryKey val id: String,
    val title: String,
    val categoryType: String, // "COLLEGE", "SCHOLARSHIP", "EXAM", "FORM"
    val relatedId: String,
    val deadlineDate: String, // "YYYY-MM-DD"
    val urgencyTag: String, // "TODAY", "THIS_WEEK", "THIS_MONTH", "UPCOMING"
    val officialUrl: String,
    val notes: String = "",
    val isReminderSet: Boolean = false,
    val verificationStatus: String = "VERIFIED",
    val lastUpdated: String = "2026-08-25"
) {
    val officialSource: String get() = "$categoryType Official Gateway"
    val applicationLink: String get() = officialUrl
}

@Entity(tableName = "applications")
data class ApplicationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "COLLEGE", "SCHOLARSHIP", "EXAM"
    val targetName: String,
    val status: String, // "PLANNING", "APPLIED", "UNDER_REVIEW", "COMPLETED", "REJECTED", "EXPIRED"
    val deadlineDate: String,
    val appliedDate: String = "",
    val applicationNumber: String = "",
    val notes: String = "",
    val portalLink: String = ""
)

@Entity(tableName = "documents")
data class DocumentItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // "ACADEMIC", "IDENTITY", "INCOME_CASTE", "GENERAL"
    val isReady: Boolean = false,
    val description: String = "",
    val issuingAuthority: String = ""
)

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetDate: String,
    val targetTime: String = "10:00 AM",
    val isCompleted: Boolean = false,
    val relatedCategory: String = "DEADLINE"
)

@Entity(tableName = "saved_items")
data class SavedItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemType: String, // "COLLEGE", "SCHOLARSHIP", "EXAM", "DEADLINE"
    val itemId: String,
    val title: String,
    val subtitle: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_questions")
data class CommunityQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorQualification: String,
    val title: String,
    val body: String,
    val tag: String,
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val answerCount: Int = 0,
    val isResolved: Boolean = false,
    val isReported: Boolean = false
)

@Entity(tableName = "community_answers")
data class CommunityAnswer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val authorName: String,
    val authorRole: String = "Student",
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val isHelpful: Boolean = false,
    val isReported: Boolean = false
)

@Entity(tableName = "reports")
data class ReportItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // "QUESTION", "ANSWER", "OPPORTUNITY"
    val targetId: String,
    val reason: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // "PENDING", "REVIEWED", "DISMISSED"
)

@Entity(tableName = "announcements")
data class AnnouncementItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: String,
    val isImportant: Boolean = false
)

// Smart Match Result Model
data class MatchResult(
    val status: MatchStatus,
    val reason: String,
    val scorePercentage: Int
)

enum class MatchStatus {
    STRONG,   // 🟢 Strong Match
    POSSIBLE, // 🟡 Possible Match
    NO_MATCH  // 🔴 Doesn't Match
}

data class OpportunityDataAuditReport(
    val totalColleges: Int,
    val totalScholarships: Int,
    val totalExams: Int,
    val totalDeadlines: Int,
    val duplicateIdCount: Int,
    val missingUrlCount: Int,
    val expiredDeadlinesCount: Int,
    val verifiedPercentage: Int,
    val issuesFound: List<String>
)
