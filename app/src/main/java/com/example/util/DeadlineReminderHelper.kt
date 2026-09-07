package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MilestoneReminder(
    val stageTag: String,       // "30_DAYS", "7_DAYS", "1_DAY", "DEADLINE_DAY"
    val stageLabel: String,     // "30 Days Before", "7 Days Before", "1 Day Before", "Deadline Day"
    val targetDate: String,     // "yyyy-MM-dd"
    val alertTitle: String,     // e.g. "[30 Days Alert] NSP Post Matric"
    val isApplicable: Boolean,  // targetDate >= today
    val isToday: Boolean        // targetDate == today
)

object DeadlineReminderHelper {

    private const val DATE_FORMAT = "yyyy-MM-dd"

    fun getTodayDateString(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())
    }

    /**
     * Calculates the 4 required milestone alerts for any deadline:
     * 1. 30 Days Before
     * 2. 7 Days Before
     * 3. 1 Day Before
     * 4. Deadline Day (Today / final closing date)
     */
    fun calculateMilestoneAlerts(
        deadlineDateStr: String,
        opportunityTitle: String,
        referenceDateStr: String = getTodayDateString()
    ): List<MilestoneReminder> {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        val deadlineDate = try {
            sdf.parse(deadlineDateStr) ?: return emptyList()
        } catch (e: Exception) {
            return emptyList()
        }

        val stages = listOf(
            Triple("30_DAYS", "30 Days Before", -30),
            Triple("7_DAYS", "7 Days Before", -7),
            Triple("1_DAY", "1 Day Before", -1),
            Triple("DEADLINE_DAY", "Deadline Day", 0)
        )

        val milestones = mutableListOf<MilestoneReminder>()

        for ((tag, label, dayOffset) in stages) {
            val cal = Calendar.getInstance()
            cal.time = deadlineDate
            if (dayOffset != 0) {
                cal.add(Calendar.DAY_OF_YEAR, dayOffset)
            }
            val milestoneDateStr = sdf.format(cal.time)
            val isApplicable = milestoneDateStr >= referenceDateStr
            val isToday = milestoneDateStr == referenceDateStr

            val prefix = when (tag) {
                "30_DAYS" -> "[30 Days Alert]"
                "7_DAYS" -> "[7 Days Alert]"
                "1_DAY" -> "[1 Day Urgent Alert]"
                else -> "[Deadline Day Alert]"
            }

            milestones.add(
                MilestoneReminder(
                    stageTag = tag,
                    stageLabel = label,
                    targetDate = milestoneDateStr,
                    alertTitle = "$prefix $opportunityTitle",
                    isApplicable = isApplicable,
                    isToday = isToday
                )
            )
        }

        return milestones
    }

    /**
     * Calculates days remaining from reference date to deadline date.
     */
    fun getDaysRemaining(
        deadlineDateStr: String,
        referenceDateStr: String = getTodayDateString()
    ): Long {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        return try {
            val dDate = sdf.parse(deadlineDateStr)?.time ?: return 0L
            val rDate = sdf.parse(referenceDateStr)?.time ?: return 0L
            val diffMs = dDate - rDate
            diffMs / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            0L
        }
    }
}
