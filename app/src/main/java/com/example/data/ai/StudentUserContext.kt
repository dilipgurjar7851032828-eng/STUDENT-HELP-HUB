package com.example.data.ai

import com.example.data.model.ApplicationItem
import com.example.data.model.SavedItem
import com.example.data.model.StudentProfile

/**
 * Encapsulates the student's personal educational context:
 * Profile details, saved bookmarks, ongoing tracked applications, and user preferences.
 */
data class StudentUserContext(
    val profile: StudentProfile? = null,
    val savedItems: List<SavedItem> = emptyList(),
    val applications: List<ApplicationItem> = emptyList(),
    val languagePreference: String = "Hinglish"
) {
    fun buildContextSummary(): String {
        val sb = StringBuilder()
        if (profile != null) {
            sb.append("Student Profile: Name=${profile.fullName}, State=${profile.state}, Qualification=${profile.qualification}, ")
            sb.append("Stream=${profile.stream}, Category=${profile.category}, Marks=${profile.marksPercentage}%, ")
            sb.append("Income=${profile.annualIncomeRange}, Preferred Course=${profile.preferredCourse}, HostelNeeded=${profile.isHostelNeeded}.\n")
        } else {
            sb.append("Student Profile: Not set.\n")
        }

        if (savedItems.isNotEmpty()) {
            sb.append("Saved Bookmarks (${savedItems.size}):\n")
            savedItems.take(6).forEach { item ->
                sb.append(" - [${item.itemType}] ${item.title} (${item.subtitle})\n")
            }
        } else {
            sb.append("Saved Bookmarks: None yet.\n")
        }

        if (applications.isNotEmpty()) {
            sb.append("Application Tracker (${applications.size} items):\n")
            applications.take(6).forEach { app ->
                sb.append(" - ${app.title} [Status: ${app.status}, Deadline: ${app.deadlineDate}, Target: ${app.targetName}]\n")
            }
        } else {
            sb.append("Application Tracker: None tracked yet.\n")
        }

        sb.append("Preferred Response Language: $languagePreference")
        return sb.toString()
    }
}
