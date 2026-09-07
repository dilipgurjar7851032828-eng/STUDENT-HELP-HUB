package com.example.data.repository

import com.example.data.local.StudentHubDao
import com.example.data.model.AnnouncementItem
import com.example.data.model.ApplicationItem
import com.example.data.model.CollegeItem
import com.example.data.model.CommunityAnswer
import com.example.data.model.CommunityQuestion
import com.example.data.model.DeadlineItem
import com.example.data.model.DocumentItem
import com.example.data.model.ExamItem
import com.example.data.model.ReminderItem
import com.example.data.model.ReportItem
import com.example.data.model.SavedItem
import com.example.data.model.ScholarshipItem
import com.example.data.model.StudentProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StudentHubRepository(private val dao: StudentHubDao) {

    val studentProfile: Flow<StudentProfile?> = dao.getStudentProfile()
    val allColleges: Flow<List<CollegeItem>> = dao.getAllColleges()
    val allScholarships: Flow<List<ScholarshipItem>> = dao.getAllScholarships()
    val allExams: Flow<List<ExamItem>> = dao.getAllExams()
    val allDeadlines: Flow<List<DeadlineItem>> = dao.getAllDeadlines()
    val allApplications: Flow<List<ApplicationItem>> = dao.getAllApplications()
    val allDocuments: Flow<List<DocumentItem>> = dao.getAllDocuments()
    val allReminders: Flow<List<ReminderItem>> = dao.getAllReminders()
    val allSavedItems: Flow<List<SavedItem>> = dao.getAllSavedItems()
    val allQuestions: Flow<List<CommunityQuestion>> = dao.getAllQuestions()
    val allQuestionsForAdmin: Flow<List<CommunityQuestion>> = dao.getAllQuestionsForAdmin()
    val allReports: Flow<List<ReportItem>> = dao.getAllReports()
    val allAnnouncements: Flow<List<AnnouncementItem>> = dao.getAllAnnouncements()

    suspend fun saveProfile(profile: StudentProfile) {
        dao.insertProfile(profile)
    }

    suspend fun toggleSaveItem(itemType: String, itemId: String, title: String, subtitle: String): Boolean {
        val alreadySaved = dao.isItemSaved(itemId)
        return if (alreadySaved) {
            dao.deleteSavedItemByItemId(itemId)
            false
        } else {
            dao.insertSavedItem(
                SavedItem(
                    itemType = itemType,
                    itemId = itemId,
                    title = title,
                    subtitle = subtitle
                )
            )
            unlockBadge("FIRST_SAVE")
            true
        }
    }

    fun isItemSaved(itemId: String): Flow<Boolean> = dao.isItemSavedFlow(itemId)

    suspend fun toggleDocumentStatus(docId: Long, isReady: Boolean) {
        dao.updateDocumentStatus(docId, isReady)
        checkAllDocsReadyBadge()
    }

    suspend fun addDocument(name: String, category: String, description: String, authority: String) {
        dao.insertDocument(
            DocumentItem(
                name = name,
                category = category,
                isReady = false,
                description = description,
                issuingAuthority = authority
            )
        )
    }

    suspend fun addApplication(app: ApplicationItem) {
        dao.insertApplication(app)
        unlockBadge("APPLICATION_TRACKER")
    }

    suspend fun updateApplication(app: ApplicationItem) {
        dao.updateApplication(app)
    }

    suspend fun deleteApplication(id: Long) {
        dao.deleteApplicationById(id)
    }

    suspend fun addReminder(title: String, targetDate: String, targetTime: String = "10:00 AM", category: String = "DEADLINE") {
        dao.insertReminder(
            ReminderItem(
                title = title,
                targetDate = targetDate,
                targetTime = targetTime,
                relatedCategory = category
            )
        )
    }

    suspend fun deleteReminder(id: Long) {
        dao.deleteReminderById(id)
    }

    suspend fun toggleDeadlineReminder(id: String, isSet: Boolean) {
        dao.updateDeadlineReminder(id, isSet)
    }

    suspend fun postQuestion(title: String, body: String, tag: String): Long {
        val currentProfile = studentProfile.firstOrNull()
        val authorName = currentProfile?.fullName?.ifBlank { "Student" } ?: "Student"
        val authorQual = currentProfile?.qualification ?: "12th Standard"
        val qId = dao.insertQuestion(
            CommunityQuestion(
                authorName = authorName,
                authorQualification = authorQual,
                title = title,
                body = body,
                tag = tag
            )
        )
        unlockBadge("HELPFUL_PEER")
        return qId
    }

    suspend fun upvoteQuestion(id: Long) {
        dao.upvoteQuestion(id)
    }

    suspend fun reportQuestion(id: Long, reason: String) {
        dao.reportQuestion(id)
        dao.insertReport(
            ReportItem(
                targetType = "QUESTION",
                targetId = id.toString(),
                reason = reason,
                details = "Reported by community user"
            )
        )
    }

    fun getAnswersForQuestion(questionId: Long): Flow<List<CommunityAnswer>> {
        return dao.getAnswersForQuestion(questionId)
    }

    suspend fun postAnswer(questionId: Long, body: String) {
        val currentProfile = studentProfile.firstOrNull()
        val authorName = currentProfile?.fullName?.ifBlank { "Senior Student" } ?: "Senior Student"
        dao.insertAnswer(
            CommunityAnswer(
                questionId = questionId,
                authorName = authorName,
                authorRole = "Student Contributor",
                body = body
            )
        )
        dao.incrementQuestionAnswerCount(questionId)
        unlockBadge("HELPFUL_PEER")
    }

    suspend fun upvoteAnswer(id: Long) {
        dao.upvoteAnswer(id)
    }

    suspend fun submitReport(targetType: String, targetId: String, reason: String, details: String) {
        dao.insertReport(
            ReportItem(
                targetType = targetType,
                targetId = targetId,
                reason = reason,
                details = details
            )
        )
    }

    suspend fun updateReportStatus(reportId: Long, status: String) {
        dao.updateReportStatus(reportId, status)
    }

    // Admin CRUD
    suspend fun addOrUpdateCollege(college: CollegeItem) {
        dao.insertCollege(college)
    }

    suspend fun deleteCollege(id: String) {
        dao.deleteCollegeById(id)
    }

    suspend fun updateCollegeVerification(id: String, status: String) {
        dao.updateCollegeVerification(id, status)
    }

    suspend fun updateCollegeVerificationWithDate(id: String, status: String, lastUpdated: String) {
        dao.updateCollegeVerificationWithDate(id, status, lastUpdated)
    }

    suspend fun addOrUpdateScholarship(scholarship: ScholarshipItem) {
        dao.insertScholarship(scholarship)
    }

    suspend fun deleteScholarship(id: String) {
        dao.deleteScholarshipById(id)
    }

    suspend fun updateScholarshipVerification(id: String, status: String) {
        dao.updateScholarshipVerification(id, status)
    }

    suspend fun updateScholarshipVerificationWithDate(id: String, status: String, lastUpdated: String) {
        dao.updateScholarshipVerificationWithDate(id, status, lastUpdated)
    }

    suspend fun addOrUpdateExam(exam: ExamItem) {
        dao.insertExam(exam)
    }

    suspend fun deleteExam(id: String) {
        dao.deleteExamById(id)
    }

    suspend fun updateExamVerificationWithDate(id: String, status: String, lastUpdated: String) {
        dao.updateExamVerificationWithDate(id, status, lastUpdated)
    }

    suspend fun addDeadline(deadline: DeadlineItem) {
        dao.insertDeadline(deadline)
    }

    suspend fun updateDeadlineVerificationWithDate(id: String, status: String, lastUpdated: String) {
        dao.updateDeadlineVerificationWithDate(id, status, lastUpdated)
    }

    suspend fun deleteDeadline(id: String) {
        dao.deleteDeadlineById(id)
    }

    suspend fun addAnnouncement(title: String, content: String, isImportant: Boolean) {
        dao.insertAnnouncement(
            AnnouncementItem(
                title = title,
                content = content,
                date = "2026-08-30",
                isImportant = isImportant
            )
        )
    }

    suspend fun deleteAnnouncement(id: Long) {
        dao.deleteAnnouncement(id)
    }

    suspend fun unlockBadge(badgeKey: String) {
        val current = studentProfile.firstOrNull() ?: return
        val currentBadges = current.badgesUnlocked.split(",").filter { it.isNotBlank() }.toMutableSet()
        if (!currentBadges.contains(badgeKey)) {
            currentBadges.add(badgeKey)
            dao.insertProfile(current.copy(badgesUnlocked = currentBadges.joinToString(",")))
        }
    }

    private suspend fun checkAllDocsReadyBadge() {
        val docs = dao.getAllDocuments().firstOrNull().orEmpty()
        if (docs.isNotEmpty() && docs.all { it.isReady }) {
            unlockBadge("DOCS_MASTER")
        }
    }

    suspend fun clearStudentPersonalData() {
        dao.clearApplications()
        dao.clearSavedItems()
        dao.clearReminders()
        dao.resetDocuments()
        val current = studentProfile.firstOrNull()
        if (current != null) {
            dao.insertProfile(
                current.copy(
                    fullName = "Guest Student",
                    email = "",
                    isGuest = true,
                    referralCount = 0,
                    badgesUnlocked = "GUEST"
                )
            )
        }
    }
}
