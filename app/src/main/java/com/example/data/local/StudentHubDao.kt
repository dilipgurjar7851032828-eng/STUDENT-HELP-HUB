package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface StudentHubDao {

    // Profile
    @Query("SELECT * FROM student_profiles WHERE id = 1")
    fun getStudentProfile(): Flow<StudentProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfile)

    // Colleges
    @Query("SELECT * FROM colleges ORDER BY viewCount DESC")
    fun getAllColleges(): Flow<List<CollegeItem>>

    @Query("SELECT * FROM colleges WHERE id = :id LIMIT 1")
    suspend fun getCollegeById(id: String): CollegeItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColleges(colleges: List<CollegeItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollege(college: CollegeItem)

    @Query("DELETE FROM colleges WHERE id = :id")
    suspend fun deleteCollegeById(id: String)

    @Query("UPDATE colleges SET verificationStatus = :status WHERE id = :id")
    suspend fun updateCollegeVerification(id: String, status: String)

    @Query("UPDATE colleges SET verificationStatus = :status, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateCollegeVerificationWithDate(id: String, status: String, lastUpdated: String)

    // Scholarships
    @Query("SELECT * FROM scholarships ORDER BY viewCount DESC")
    fun getAllScholarships(): Flow<List<ScholarshipItem>>

    @Query("SELECT * FROM scholarships WHERE id = :id LIMIT 1")
    suspend fun getScholarshipById(id: String): ScholarshipItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScholarships(scholarships: List<ScholarshipItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScholarship(scholarship: ScholarshipItem)

    @Query("DELETE FROM scholarships WHERE id = :id")
    suspend fun deleteScholarshipById(id: String)

    @Query("UPDATE scholarships SET verificationStatus = :status WHERE id = :id")
    suspend fun updateScholarshipVerification(id: String, status: String)

    @Query("UPDATE scholarships SET verificationStatus = :status, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateScholarshipVerificationWithDate(id: String, status: String, lastUpdated: String)

    // Exams
    @Query("SELECT * FROM exams ORDER BY viewCount DESC")
    fun getAllExams(): Flow<List<ExamItem>>

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: String): ExamItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamItem)

    @Query("DELETE FROM exams WHERE id = :id")
    suspend fun deleteExamById(id: String)

    @Query("UPDATE exams SET verificationStatus = :status, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateExamVerificationWithDate(id: String, status: String, lastUpdated: String)

    // Deadlines
    @Query("SELECT * FROM deadlines ORDER BY deadlineDate ASC")
    fun getAllDeadlines(): Flow<List<DeadlineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadlines(deadlines: List<DeadlineItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: DeadlineItem)

    @Query("UPDATE deadlines SET isReminderSet = :isSet WHERE id = :id")
    suspend fun updateDeadlineReminder(id: String, isSet: Boolean)

    @Query("UPDATE deadlines SET verificationStatus = :status, lastUpdated = :lastUpdated WHERE id = :id")
    suspend fun updateDeadlineVerificationWithDate(id: String, status: String, lastUpdated: String)

    @Query("DELETE FROM deadlines WHERE id = :id")
    suspend fun deleteDeadlineById(id: String)

    // Applications
    @Query("SELECT * FROM applications ORDER BY id DESC")
    fun getAllApplications(): Flow<List<ApplicationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationItem)

    @Update
    suspend fun updateApplication(app: ApplicationItem)

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplicationById(id: Long)

    // Documents
    @Query("SELECT * FROM documents ORDER BY id ASC")
    fun getAllDocuments(): Flow<List<DocumentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<DocumentItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentItem)

    @Query("UPDATE documents SET isReady = :isReady WHERE id = :id")
    suspend fun updateDocumentStatus(id: Long, isReady: Boolean)

    // Reminders
    @Query("SELECT * FROM reminders ORDER BY id DESC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderItem)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    // Saved Items
    @Query("SELECT * FROM saved_items ORDER BY savedTimestamp DESC")
    fun getAllSavedItems(): Flow<List<SavedItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_items WHERE itemId = :itemId LIMIT 1)")
    fun isItemSavedFlow(itemId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_items WHERE itemId = :itemId LIMIT 1)")
    suspend fun isItemSaved(itemId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedItem(item: SavedItem)

    @Query("DELETE FROM saved_items WHERE itemId = :itemId")
    suspend fun deleteSavedItemByItemId(itemId: String)

    // Community Q&A
    @Query("SELECT * FROM community_questions WHERE isReported = 0 ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<CommunityQuestion>>

    @Query("SELECT * FROM community_questions ORDER BY id DESC")
    fun getAllQuestionsForAdmin(): Flow<List<CommunityQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: CommunityQuestion): Long

    @Query("UPDATE community_questions SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvoteQuestion(id: Long)

    @Query("UPDATE community_questions SET isReported = 1 WHERE id = :id")
    suspend fun reportQuestion(id: Long)

    @Query("DELETE FROM community_questions WHERE id = :id")
    suspend fun deleteQuestion(id: Long)

    @Query("SELECT * FROM community_answers WHERE questionId = :questionId AND isReported = 0 ORDER BY upvotes DESC, id ASC")
    fun getAnswersForQuestion(questionId: Long): Flow<List<CommunityAnswer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: CommunityAnswer)

    @Query("UPDATE community_questions SET answerCount = answerCount + 1 WHERE id = :questionId")
    suspend fun incrementQuestionAnswerCount(questionId: Long)

    @Query("UPDATE community_answers SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvoteAnswer(id: Long)

    @Query("UPDATE community_answers SET isReported = 1 WHERE id = :id")
    suspend fun reportAnswer(id: Long)

    @Query("DELETE FROM community_answers WHERE id = :id")
    suspend fun deleteAnswer(id: Long)

    // Reports
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportItem)

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementItem)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)

    // Clear all user-specific data (for account deletion / privacy data wipe)
    @Query("DELETE FROM applications")
    suspend fun clearApplications()

    @Query("DELETE FROM saved_items")
    suspend fun clearSavedItems()

    @Query("DELETE FROM reminders")
    suspend fun clearReminders()

    @Query("UPDATE documents SET isReady = 0")
    suspend fun resetDocuments()
}
