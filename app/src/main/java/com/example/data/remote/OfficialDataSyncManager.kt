package com.example.data.remote

import android.util.Log
import com.example.data.model.CollegeItem
import com.example.data.model.ExamItem
import com.example.data.model.ScholarshipItem
import com.example.data.repository.StudentHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

data class OfficialSyncReport(
    val totalChecked: Int = 0,
    val verifiedCount: Int = 0,
    val needsVerificationCount: Int = 0,
    val expiredCount: Int = 0,
    val workingConnections: List<String> = emptyList(),
    val pendingConfigConnections: List<String> = emptyList(),
    val portalStatuses: List<OfficialSourceConnectionStatus> = emptyList(),
    val timestamp: String = ""
)

object OfficialDataSyncManager {

    private const val TAG = "OfficialDataSyncManager"

    suspend fun performFullLiveVerification(
        repository: StudentHubRepository
    ): OfficialSyncReport = withContext(Dispatchers.IO) {
        val today = OfficialSourceVerifier.getTodayDate()
        Log.i(TAG, "Starting live verification from official student information sources...")

        var verifiedCount = 0
        var needsVerificationCount = 0
        var expiredCount = 0

        val working = mutableListOf<String>()
        val pending = mutableListOf<String>()

        // 1. Probe core official government & university portals
        val portalStatuses = OfficialSourceVerifier.checkAllOfficialPortals()
        portalStatuses.forEach { portal ->
            if (portal.isLive) {
                working.add("${portal.name} (${portal.portalUrl})")
            } else if (portal.requiresBackendConfig) {
                pending.add("${portal.name} - ${portal.connectionNote}")
            } else {
                pending.add("${portal.name} (${portal.portalUrl}) - Needs Direct Intranet/Partner API Configuration")
            }
        }

        // 2. Live verification of Scholarships
        val scholarships = repository.allScholarships.firstOrNull() ?: emptyList()
        for (sch in scholarships) {
            val result = OfficialSourceVerifier.verifyOfficialSource(
                url = sch.officialWebsite,
                deadlineDate = sch.deadline
            )
            val newStatus = result.status
            val newDate = if (result.status == "VERIFIED") today else sch.lastUpdated

            when (newStatus) {
                "VERIFIED" -> verifiedCount++
                "EXPIRED" -> expiredCount++
                else -> needsVerificationCount++
            }

            if (newStatus != sch.verificationStatus || newDate != sch.lastUpdated) {
                repository.updateScholarshipVerificationWithDate(sch.id, newStatus, newDate)
            }
        }

        // 3. Live verification of Colleges & Universities
        val colleges = repository.allColleges.firstOrNull() ?: emptyList()
        for (col in colleges) {
            val result = OfficialSourceVerifier.verifyOfficialSource(
                url = col.officialWebsite,
                deadlineDate = col.admissionDeadline
            )
            val newStatus = result.status
            val newDate = if (result.status == "VERIFIED") today else col.lastUpdated

            when (newStatus) {
                "VERIFIED" -> verifiedCount++
                "EXPIRED" -> expiredCount++
                else -> needsVerificationCount++
            }

            if (newStatus != col.verificationStatus || newDate != col.lastUpdated) {
                repository.updateCollegeVerificationWithDate(col.id, newStatus, newDate)
            }
        }

        // 4. Live verification of Examination Bodies & Portals
        val exams = repository.allExams.firstOrNull() ?: emptyList()
        for (ex in exams) {
            val result = OfficialSourceVerifier.verifyOfficialSource(
                url = ex.officialWebsite,
                deadlineDate = ex.applicationDeadline
            )
            val newStatus = result.status
            val newDate = if (result.status == "VERIFIED") today else ex.lastUpdated

            when (newStatus) {
                "VERIFIED" -> verifiedCount++
                "EXPIRED" -> expiredCount++
                else -> needsVerificationCount++
            }

            if (newStatus != ex.verificationStatus || newDate != ex.lastUpdated) {
                repository.updateExamVerificationWithDate(ex.id, newStatus, newDate)
            }
        }

        // 5. Live verification of Deadlines
        val deadlines = repository.allDeadlines.firstOrNull() ?: emptyList()
        for (dl in deadlines) {
            val result = OfficialSourceVerifier.verifyOfficialSource(
                url = dl.officialUrl,
                deadlineDate = dl.deadlineDate
            )
            val newStatus = result.status
            val newDate = if (result.status == "VERIFIED") today else dl.lastUpdated

            when (newStatus) {
                "VERIFIED" -> verifiedCount++
                "EXPIRED" -> expiredCount++
                else -> needsVerificationCount++
            }

            if (newStatus != dl.verificationStatus || newDate != dl.lastUpdated) {
                repository.updateDeadlineVerificationWithDate(dl.id, newStatus, newDate)
            }
        }

        val total = scholarships.size + colleges.size + exams.size + deadlines.size
        Log.i(TAG, "Sync complete: Total=$total, Verified=$verifiedCount, NeedsVerification=$needsVerificationCount, Expired=$expiredCount")

        OfficialSyncReport(
            totalChecked = total,
            verifiedCount = verifiedCount,
            needsVerificationCount = needsVerificationCount,
            expiredCount = expiredCount,
            workingConnections = working,
            pendingConfigConnections = pending,
            portalStatuses = portalStatuses,
            timestamp = today
        )
    }
}
