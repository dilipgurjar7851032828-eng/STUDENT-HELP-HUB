package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollegeItem
import com.example.data.model.ExamItem
import com.example.data.model.MatchResult
import com.example.data.model.MatchStatus
import com.example.data.model.ScholarshipItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.OfficialSourceButton
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

sealed class PersonalizedMatchItem {
    abstract val id: String
    abstract val title: String
    abstract val categoryLabel: String
    abstract val deadlineDate: String
    abstract val officialUrl: String
    abstract val matchResult: MatchResult
    abstract val matchExplanation: List<String>
    abstract val isStrongMatch: Boolean

    data class ScholarshipMatch(
        val item: ScholarshipItem,
        override val matchResult: MatchResult,
        override val matchExplanation: List<String>,
        override val isStrongMatch: Boolean
    ) : PersonalizedMatchItem() {
        override val id: String = item.id
        override val title: String = item.title
        override val categoryLabel: String = "Scholarship • ${item.provider}"
        override val deadlineDate: String = item.deadline
        override val officialUrl: String = item.officialWebsite
    }

    data class CollegeMatch(
        val item: CollegeItem,
        override val matchResult: MatchResult,
        override val matchExplanation: List<String>,
        override val isStrongMatch: Boolean
    ) : PersonalizedMatchItem() {
        override val id: String = item.id
        override val title: String = item.name
        override val categoryLabel: String = "${item.city}, ${item.state} • ${if (item.isGovernment) "Govt" else "Private"}"
        override val deadlineDate: String = item.admissionDeadline
        override val officialUrl: String = item.officialWebsite
    }

    data class ExamMatch(
        val item: ExamItem,
        override val matchResult: MatchResult,
        override val matchExplanation: List<String>,
        override val isStrongMatch: Boolean
    ) : PersonalizedMatchItem() {
        override val id: String = item.id
        override val title: String = item.title
        override val categoryLabel: String = "Entrance Exam • ${item.conductingBody}"
        override val deadlineDate: String = item.applicationDeadline
        override val officialUrl: String = item.officialWebsite
    }
}

@Composable
fun MereLiyeDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val colleges by viewModel.colleges.collectAsState()
    val scholarships by viewModel.scholarships.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val applications by viewModel.applications.collectAsState()

    var selectedTab by remember { mutableStateOf("ALL") }

    // Compute Personalized Matches based on Student Profile
    val personalizedMatches = remember(profile, colleges, scholarships, exams) {
        val matches = mutableListOf<PersonalizedMatchItem>()

        // 1. Scholarships matching
        for (sch in scholarships) {
            val match = viewModel.getScholarshipMatch(sch)
            val isStrong = match.status == MatchStatus.STRONG
            val reasons = listOf(
                match.reason,
                "Eligible for ${sch.eligibleCategories} • ₹${sch.amountPerYear}"
            )
            matches.add(
                PersonalizedMatchItem.ScholarshipMatch(
                    item = sch,
                    matchResult = match,
                    matchExplanation = reasons,
                    isStrongMatch = isStrong
                )
            )
        }

        // 2. Colleges matching
        for (col in colleges) {
            val match = viewModel.getCollegeMatch(col)
            val isStrong = match.status == MatchStatus.STRONG
            val reasons = listOf(
                match.reason,
                "Courses: ${col.coursesOffered} • Fees: ${col.annualFees}"
            )
            matches.add(
                PersonalizedMatchItem.CollegeMatch(
                    item = col,
                    matchResult = match,
                    matchExplanation = reasons,
                    isStrongMatch = isStrong
                )
            )
        }

        // 3. Exams matching
        val profStream = profile?.stream ?: ""
        for (ex in exams) {
            val isRelevant = profStream.isBlank() ||
                    ex.title.contains("JEE", ignoreCase = true) && profStream.contains("Science", ignoreCase = true) ||
                    ex.title.contains("NEET", ignoreCase = true) && profStream.contains("Biology", ignoreCase = true) ||
                    ex.title.contains("CUET", ignoreCase = true) ||
                    ex.eligibility.contains("12th", ignoreCase = true)
            val isStrong = isRelevant
            val matchRes = MatchResult(
                status = if (isStrong) MatchStatus.STRONG else MatchStatus.POSSIBLE,
                reason = if (isStrong) "Matches your academic stream and eligibility" else "Eligible entrance exam",
                scorePercentage = if (isStrong) 85 else 60
            )
            val reasons = listOf(
                matchRes.reason,
                "Target: ${ex.coursesTargeted} • Exam Date: ${ex.examDate}"
            )
            matches.add(
                PersonalizedMatchItem.ExamMatch(
                    item = ex,
                    matchResult = matchRes,
                    matchExplanation = reasons,
                    isStrongMatch = isStrong
                )
            )
        }

        matches.sortedWith(
            compareByDescending<PersonalizedMatchItem> { it.isStrongMatch }
                .thenByDescending { it.matchResult.scorePercentage }
        )
    }

    val filteredMatches = when (selectedTab) {
        "SCHOLARSHIPS" -> personalizedMatches.filterIsInstance<PersonalizedMatchItem.ScholarshipMatch>()
        "COLLEGES" -> personalizedMatches.filterIsInstance<PersonalizedMatchItem.CollegeMatch>()
        "EXAMS" -> personalizedMatches.filterIsInstance<PersonalizedMatchItem.ExamMatch>()
        else -> personalizedMatches
    }

    val strongMatchCount = personalizedMatches.count { it.isStrongMatch }
    val scholarshipCount = personalizedMatches.filterIsInstance<PersonalizedMatchItem.ScholarshipMatch>().size
    val collegeCount = personalizedMatches.filterIsInstance<PersonalizedMatchItem.CollegeMatch>().size
    val examCount = personalizedMatches.filterIsInstance<PersonalizedMatchItem.ExamMatch>().size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "Mere Liye Kya Available Hai? 🚀",
            subtitle = "Aapke Profile Ke Aadhar Par Curated Opportunities",
            onBackClick = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {
            // Student Profile Identity Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mere_liye_profile_summary"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Aapke Profile Ke Aadhar Par 🎯",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = profile?.fullName ?: "Student",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                                modifier = Modifier.testTag("edit_profile_button")
                            ) {
                                Text("Edit Profile", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Criteria chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = profile?.qualification ?: "12th",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = profile?.stream ?: "General",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = profile?.category ?: "General",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${profile?.marksPercentage ?: 75.0}% Marks",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedTab == "ALL",
                            onClick = { selectedTab = "ALL" },
                            label = { Text("Sabhi Matches ($strongMatchCount Strong)") },
                            modifier = Modifier.testTag("filter_tab_all")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTab == "SCHOLARSHIPS",
                            onClick = { selectedTab = "SCHOLARSHIPS" },
                            label = { Text("Scholarships ($scholarshipCount)") },
                            modifier = Modifier.testTag("filter_tab_scholarships")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTab == "COLLEGES",
                            onClick = { selectedTab = "COLLEGES" },
                            label = { Text("Colleges ($collegeCount)") },
                            modifier = Modifier.testTag("filter_tab_colleges")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTab == "EXAMS",
                            onClick = { selectedTab = "EXAMS" },
                            label = { Text("Exams ($examCount)") },
                            modifier = Modifier.testTag("filter_tab_exams")
                        )
                    }
                }
            }

            // Matched Opportunity Cards
            items(filteredMatches) { matchItem ->
                val isSaved = savedItems.any { it.itemId == matchItem.id }
                val isTracked = applications.any { it.title.contains(matchItem.title, ignoreCase = true) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mere_liye_card_${matchItem.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MatchStatusPill(
                                matchResult = matchItem.matchResult,
                                onClick = {
                                    viewModel.showSmartMatchDetails(matchItem.title, matchItem.matchResult)
                                }
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                VerificationBadge(status = "VERIFIED")
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        val type = when (matchItem) {
                                            is PersonalizedMatchItem.ScholarshipMatch -> "SCHOLARSHIP"
                                            is PersonalizedMatchItem.CollegeMatch -> "COLLEGE"
                                            is PersonalizedMatchItem.ExamMatch -> "EXAM"
                                        }
                                        viewModel.toggleSaveItem(type, matchItem.id, matchItem.title, matchItem.categoryLabel)
                                    },
                                    modifier = Modifier.testTag("mere_liye_save_${matchItem.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Save Bookmark",
                                        tint = if (isSaved) BrandIndigo else Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = matchItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = matchItem.categoryLabel,
                            fontSize = 12.sp,
                            color = Slate700
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Match Reasons / Eligibility breakdown
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandIndigo.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Aapke Liye Kyun Suitable Hai? ✓",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandIndigo
                            )
                            for (reason in matchItem.matchExplanation.take(2)) {
                                Text(
                                    text = "• $reason",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Crucial Action Buttons: Save + Application Tracker + 4-Stage Reminders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Track in Application Tracker
                            OutlinedButton(
                                onClick = {
                                    val cat = when (matchItem) {
                                        is PersonalizedMatchItem.ScholarshipMatch -> "SCHOLARSHIP"
                                        is PersonalizedMatchItem.CollegeMatch -> "COLLEGE"
                                        is PersonalizedMatchItem.ExamMatch -> "EXAM"
                                    }
                                    viewModel.trackOpportunity(
                                        title = matchItem.title,
                                        category = cat,
                                        targetName = matchItem.categoryLabel,
                                        deadlineDate = matchItem.deadlineDate,
                                        portalLink = matchItem.officialUrl
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mere_liye_track_${matchItem.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = if (isTracked) VerifiedGreen else BrandIndigo,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTracked) "Tracked ✓" else "Track App",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // 2. Set 4-Stage Reminders (30d, 7d, 1d, deadline day)
                            OutlinedButton(
                                onClick = {
                                    viewModel.scheduleMultiStageDeadlineReminders(
                                        deadlineTitle = matchItem.title,
                                        deadlineDate = matchItem.deadlineDate,
                                        category = matchItem.categoryLabel
                                    )
                                },
                                modifier = Modifier.testTag("mere_liye_remind_${matchItem.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = BrandAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alerts (30d/7d/1d)", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Official Link Button
                        OfficialSourceButton(
                            url = matchItem.officialUrl,
                            label = "Apply on Official Portal",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
