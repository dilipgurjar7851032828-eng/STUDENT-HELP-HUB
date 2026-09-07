package com.example.data.repository

import com.example.data.model.CollegeItem
import com.example.data.model.MatchResult
import com.example.data.model.MatchStatus
import com.example.data.model.ScholarshipItem
import com.example.data.model.StudentProfile

object SmartMatcher {

    fun matchScholarship(scholarship: ScholarshipItem, profile: StudentProfile?): MatchResult {
        if (profile == null) {
            return MatchResult(
                status = MatchStatus.POSSIBLE,
                reason = "Complete your student profile to see exact personalized eligibility.",
                scorePercentage = 50
            )
        }

        val reasons = mutableListOf<String>()
        val mismatches = mutableListOf<String>()
        var score = 0

        // 1. Category Check
        val catMatches = when {
            scholarship.eligibleCategories.contains("All", ignoreCase = true) -> {
                reasons.add("Open for all categories including ${profile.category}")
                true
            }
            scholarship.eligibleCategories.contains(profile.category, ignoreCase = true) -> {
                reasons.add("Matches your ${profile.category} category reservation")
                true
            }
            scholarship.eligibleCategories.contains("Girls", ignoreCase = true) -> {
                reasons.add("Targeted scheme for female students")
                true
            }
            else -> {
                mismatches.add("Requires category '${scholarship.eligibleCategories}', but profile is '${profile.category}'")
                false
            }
        }
        if (catMatches) score += 35

        // 2. Income Check
        val studentIncomeNum = when {
            profile.annualIncomeRange.contains("< ₹1L") -> 90000L
            profile.annualIncomeRange.contains("₹1L - ₹2.5L") -> 200000L
            profile.annualIncomeRange.contains("₹2.5L - ₹8L") -> 500000L
            else -> 900000L
        }

        val incomeMatches = if (scholarship.maxAnnualIncome >= studentIncomeNum) {
            reasons.add("Your family income range is within the scheme ceiling of ₹${scholarship.maxAnnualIncome / 100000.0} Lakh/yr")
            true
        } else {
            mismatches.add("Annual income ceiling is ₹${scholarship.maxAnnualIncome / 100000.0} Lakh, which is below your profile range")
            false
        }
        if (incomeMatches) score += 35

        // 3. Academic Marks Check
        val marksMatch = if (profile.marksPercentage >= scholarship.minPercentage) {
            reasons.add("Your marks (${profile.marksPercentage}%) satisfy minimum threshold (${scholarship.minPercentage}%)")
            true
        } else {
            mismatches.add("Requires min ${scholarship.minPercentage}% marks (profile has ${profile.marksPercentage}%)")
            false
        }
        if (marksMatch) score += 30

        return when {
            mismatches.isEmpty() && score >= 90 -> {
                MatchResult(
                    status = MatchStatus.STRONG,
                    reason = reasons.joinToString(". "),
                    scorePercentage = score
                )
            }
            mismatches.size == 1 && (incomeMatches || catMatches) -> {
                MatchResult(
                    status = MatchStatus.POSSIBLE,
                    reason = "Partial Match: " + (reasons + mismatches).joinToString(". "),
                    scorePercentage = (score * 0.7).toInt().coerceIn(30, 75)
                )
            }
            else -> {
                MatchResult(
                    status = MatchStatus.NO_MATCH,
                    reason = "Doesn't Match: " + mismatches.joinToString(". "),
                    scorePercentage = (score * 0.3).toInt().coerceAtMost(25)
                )
            }
        }
    }

    fun matchCollege(college: CollegeItem, profile: StudentProfile?): MatchResult {
        if (profile == null) {
            return MatchResult(
                status = MatchStatus.POSSIBLE,
                reason = "Complete your profile for personalized college compatibility.",
                scorePercentage = 50
            )
        }

        val reasons = mutableListOf<String>()
        val mismatches = mutableListOf<String>()
        var score = 40 // base institutional quality score

        // Location match
        if (college.state.equals(profile.state, ignoreCase = true)) {
            score += 25
            reasons.add("Located in your home state (${profile.state}) - eligible for Home State quota / lower travel")
        } else if (college.university.contains("National", ignoreCase = true) || college.university.contains("Central", ignoreCase = true)) {
            score += 20
            reasons.add("Central / National Institute open to all-India merit")
        }

        // Stream / Course match
        val streamMatches = when {
            profile.stream.contains("Science", ignoreCase = true) && (college.coursesOffered.contains("B.Tech", ignoreCase = true) || college.coursesOffered.contains("MBBS", ignoreCase = true) || college.coursesOffered.contains("B.Sc", ignoreCase = true)) -> {
                score += 25
                reasons.add("Offers technical & science degrees matching your PCM/PCB stream")
                true
            }
            profile.stream.contains("Commerce", ignoreCase = true) && (college.coursesOffered.contains("B.Com", ignoreCase = true) || college.coursesOffered.contains("Economics", ignoreCase = true)) -> {
                score += 25
                reasons.add("Offers top commerce & finance programs for your background")
                true
            }
            profile.stream.contains("Arts", ignoreCase = true) && (college.coursesOffered.contains("B.A.", ignoreCase = true) || college.coursesOffered.contains("LLB", ignoreCase = true)) -> {
                score += 25
                reasons.add("Offers humanities, law & social science disciplines")
                true
            }
            else -> {
                score += 10
                reasons.add("Offers diverse undergraduate programs")
                false
            }
        }

        // Hostel match
        if (profile.isHostelNeeded && college.hostelAvailable) {
            score += 10
            reasons.add("On-campus hostel accommodations available")
        }

        return when {
            score >= 80 -> MatchResult(
                status = MatchStatus.STRONG,
                reason = reasons.joinToString(". "),
                scorePercentage = score.coerceAtMost(98)
            )
            score >= 50 -> MatchResult(
                status = MatchStatus.POSSIBLE,
                reason = reasons.joinToString(". "),
                scorePercentage = score
            )
            else -> MatchResult(
                status = MatchStatus.NO_MATCH,
                reason = mismatches.ifEmpty { listOf("Stream or criteria not closely aligned with your current preference") }.joinToString(". "),
                scorePercentage = score.coerceAtMost(35)
            )
        }
    }
}
