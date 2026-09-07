package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollegeItem
import com.example.data.model.ScholarshipItem
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.VerificationBadge
import com.example.ui.components.OfficialSourcesStatusDialog
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandAmberLight
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandIndigoDark
import com.example.ui.theme.BrandIndigoLight
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val colleges by viewModel.colleges.collectAsState()
    val scholarships by viewModel.scholarships.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val deadlines by viewModel.deadlines.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val docCompletion by viewModel.documentCompletionPercentage.collectAsState()

    var searchInput by remember { mutableStateOf("") }
    val parsedIntent by viewModel.parsedSearchIntent.collectAsState()
    val showWeeklySummaryDialog by viewModel.showWeeklySummary.collectAsState()
    val isSyncingOfficialData by viewModel.isSyncingOfficialData.collectAsState()
    val officialSyncReport by viewModel.officialSyncReport.collectAsState()
    var showOfficialSourcesDialog by remember { mutableStateOf(false) }

    // Calculate Smart Matching counts for the student
    val strongColleges = colleges.count { viewModel.getCollegeMatch(it).status.name == "STRONG" }
    val strongScholarships = scholarships.count { viewModel.getScholarshipMatch(it).status.name == "STRONG" }
    val totalStrongMatches = strongColleges + strongScholarships

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // App Header / Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BrandIndigoDark, BrandIndigo)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BrandAmber,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = BrandIndigoDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Student Help Hub",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = if (profile?.isGuest == true) "Namaste, Guest Student" else "Namaste, ${profile?.fullName?.ifBlank { "Student" } ?: "Student"} 👋",
                                color = BrandAmberLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.showWeeklySummary.value = true },
                            modifier = Modifier.testTag("weekly_summary_bell")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Weekly Summary",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                            modifier = Modifier.testTag("profile_avatar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "“Student ki padhai se career tak, sab ek jagah.”",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Natural Voice & Text Search Bar
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    textStyle = TextStyle(
                        color = Slate900,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    placeholder = {
                        Text(
                            text = "Search: '12th ke baad Delhi mein govt college...'",
                            fontSize = 13.sp,
                            color = Slate700
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BrandIndigo
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchInput.isNotBlank()) {
                                IconButton(onClick = {
                                    searchInput = ""
                                    viewModel.clearNaturalSearch()
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                            IconButton(
                                onClick = {
                                    // Natural language search query simulation
                                    val prompt = searchInput.ifBlank { "12th ke baad Delhi mein government college batao" }
                                    searchInput = prompt
                                    viewModel.performNaturalSearch(prompt)
                                },
                                modifier = Modifier.testTag("voice_search_trigger")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Search",
                                    tint = BrandIndigo
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Slate900,
                        unfocusedTextColor = Slate900,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = BrandIndigo,
                        focusedBorderColor = BrandAmber,
                        unfocusedBorderColor = Color.Transparent,
                        focusedPlaceholderColor = Slate700,
                        unfocusedPlaceholderColor = Slate700,
                        focusedLeadingIconColor = BrandIndigo,
                        unfocusedLeadingIconColor = BrandIndigo
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchInput.isNotBlank()) {
                            viewModel.performNaturalSearch(searchInput)
                        }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("natural_search_input")
                )
            }
        }

        // Search Parsing Notification (if active)
        if (parsedIntent != null) {
            Surface(
                color = BrandAmber.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = parsedIntent?.explanation.orEmpty(),
                        fontSize = 12.sp,
                        color = Slate800,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearNaturalSearch() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Official Government & University Portals Live Verification Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("official_portals_status_banner"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = VerifiedGreen.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Verified Official Sources",
                                tint = VerifiedGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Official Portals Connected",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = VerifiedGreenContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isSyncingOfficialData) "CHECKING..." else "LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerifiedGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isSyncingOfficialData) "Connecting to NSP, NTA, DU, AICTE..."
                            else "100% authentic govt, admission & scholarship records",
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSyncingOfficialData) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = BrandIndigo
                        )
                    } else {
                        IconButton(
                            onClick = { viewModel.syncWithOfficialSources() },
                            modifier = Modifier.size(32.dp).testTag("sync_official_data_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync with Official Sources",
                                tint = BrandIndigo,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = { showOfficialSourcesDialog = true },
                        modifier = Modifier.size(32.dp).testTag("view_official_sources_info")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Official Portals Status",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MAIN CTA: "Mere Liye Kya Available Hai?"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { viewModel.navigateTo(AppScreen.SCHOLARSHIP_FINDER) }
                .testTag("main_cta_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = VerifiedGreen,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "PERSONALIZED",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${profile?.qualification} • ${profile?.category}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Mere Liye Kya Available Hai? 🚀",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Aapke liye $totalStrongMatches strong matches & multiple state schemes active hain.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Opportunities",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // "Aapke Liye Naya" Section
        SectionHeader(
            title = "Aapke Liye Naya ✨",
            subtitle = "New verified opportunities aligned with your profile",
            onSeeAllClick = { viewModel.navigateTo(AppScreen.COLLEGE_FINDER) }
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(colleges.take(3)) { college ->
                val match = viewModel.getCollegeMatch(college)
                NewOpportunityCard(
                    title = college.name,
                    subtitle = "${college.city}, ${college.state} • ${college.annualFees}",
                    badgeText = college.rankingInfo,
                    matchStatus = match.status.name,
                    verificationStatus = college.verificationStatus,
                    lastVerifiedDate = college.lastUpdated,
                    onMatchClick = {
                        viewModel.smartMatchDetail.value = Pair(college.name, match)
                    },
                    onClick = {
                        viewModel.selectedOpportunityForDetail.value = college
                    }
                )
            }
            items(scholarships.take(2)) { scholarship ->
                val match = viewModel.getScholarshipMatch(scholarship)
                NewOpportunityCard(
                    title = scholarship.title,
                    subtitle = "${scholarship.amountPerYear} • Deadline: ${scholarship.deadline}",
                    badgeText = "Scholarship",
                    matchStatus = match.status.name,
                    verificationStatus = scholarship.verificationStatus,
                    lastVerifiedDate = scholarship.lastUpdated,
                    onMatchClick = {
                        viewModel.smartMatchDetail.value = Pair(scholarship.title, match)
                    },
                    onClick = {
                        viewModel.selectedOpportunityForDetail.value = scholarship
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Category Grid (12 core modules)
        Text(
            text = "Sabhi Suvidhayein (All Services)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServiceGridItem(
                    title = "College Finder",
                    subtitle = "${colleges.size} Universities",
                    icon = Icons.Default.School,
                    color = BrandIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.COLLEGE_FINDER) },
                    testTag = "service_college_finder"
                )
                ServiceGridItem(
                    title = "Scholarships",
                    subtitle = "${scholarships.size} Schemes",
                    icon = Icons.Default.MonetizationOn,
                    color = VerifiedGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.SCHOLARSHIP_FINDER) },
                    testTag = "service_scholarship_finder"
                )
                ServiceGridItem(
                    title = "Admissions & Forms",
                    subtitle = "${exams.size} Portals",
                    icon = Icons.Default.Description,
                    color = BrandAmber,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.ADMISSION_FORMS) },
                    testTag = "service_admissions"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServiceGridItem(
                    title = "Important Dates",
                    subtitle = "${deadlines.size} Deadlines",
                    icon = Icons.Default.CalendarMonth,
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.IMPORTANT_DATES) },
                    testTag = "service_important_dates"
                )
                ServiceGridItem(
                    title = "Documents",
                    subtitle = "$docCompletion% Ready",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF00897B),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.DOCUMENT_CHECKLIST) },
                    testTag = "service_documents"
                )
                ServiceGridItem(
                    title = "Application Tracker",
                    subtitle = "${applications.size} Tracked",
                    icon = Icons.Default.Timeline,
                    color = Color(0xFF5E35B1),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.APPLICATION_TRACKER) },
                    testTag = "service_tracker"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServiceGridItem(
                    title = "StudyBuddy AI",
                    subtitle = "Ask Any Doubt",
                    icon = Icons.Default.AutoAwesome,
                    color = BrandIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.STUDY_BUDDY_AI) },
                    testTag = "service_study_buddy"
                )
                ServiceGridItem(
                    title = "Saved Items",
                    subtitle = "${savedItems.size} Bookmarked",
                    icon = Icons.Default.Bookmark,
                    color = Color(0xFFD81B60),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.SAVED_ITEMS) },
                    testTag = "service_saved_items"
                )
                ServiceGridItem(
                    title = "Community Q&A",
                    subtitle = "Peer Help",
                    icon = Icons.Default.Forum,
                    color = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.COMMUNITY_QA) },
                    testTag = "service_community_qa"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trending Opportunities Section (Aggregate views)
        SectionHeader(
            title = "Trending Opportunities 🔥",
            subtitle = "Most viewed by Indian students this month",
            onSeeAllClick = { viewModel.navigateTo(AppScreen.COLLEGE_FINDER) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            colleges.take(2).forEach { col ->
                TrendingItemRow(
                    title = col.name,
                    category = "College • ${col.state}",
                    views = col.viewCount,
                    onClick = { viewModel.selectedOpportunityForDetail.value = col }
                )
            }
            scholarships.take(2).forEach { sch ->
                TrendingItemRow(
                    title = sch.title,
                    category = "Scholarship • ${sch.amountPerYear}",
                    views = sch.viewCount,
                    onClick = { viewModel.selectedOpportunityForDetail.value = sch }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly Summary CTA Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { viewModel.showWeeklySummary.value = true }
                .testTag("weekly_summary_card"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandAmber.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = BrandAmber)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aapka Weekly Student Summary 📊",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Upcoming deadlines, saved forms & readiness overview check karein.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Slate700)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Authentic Source Guarantee Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = VerifiedGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "100% Authentic Government & University notices. We never fabricate application cutoffs, deadlines, or fees.",
                    fontSize = 11.sp,
                    color = Slate700,
                    lineHeight = 15.sp
                )
            }
        }

        // Weekly Summary Dialog triggered by bell icon
        if (showWeeklySummaryDialog) {
            val urgentDeadlines = deadlines.take(3)
            val pendingApps = applications.count { it.status == "APPLIED" || it.status == "PLANNING" }

            AlertDialog(
                onDismissRequest = { viewModel.showWeeklySummary.value = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = BrandIndigo,
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = "Weekly Student Digest",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            color = BrandIndigo.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📊 Quick Overview",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BrandIndigo
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Matched Opportunities: $totalStrongMatches strong matches\n• Active Applications: $pendingApps in progress\n• Document Readiness: $docCompletion% verified",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (urgentDeadlines.isNotEmpty()) {
                            Text(
                                text = "⏰ Approaching Deadlines:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            urgentDeadlines.forEach { dl ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dl.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val isUrgent = dl.urgencyTag == "TODAY" || dl.urgencyTag == "THIS_WEEK"
                                    Surface(
                                        color = if (isUrgent) Color.Red.copy(alpha = 0.1f) else BrandIndigo.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = dl.deadlineDate,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUrgent) Color.Red else BrandIndigo,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.showWeeklySummary.value = false
                            viewModel.navigateTo(AppScreen.IMPORTANT_DATES)
                        }
                    ) {
                        Text("View All Dates")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showWeeklySummary.value = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Official Portals Connectivity & Verification Dialog
        if (showOfficialSourcesDialog) {
            OfficialSourcesStatusDialog(
                report = officialSyncReport,
                isSyncing = isSyncingOfficialData,
                onSyncClick = { viewModel.syncWithOfficialSources() },
                onDismiss = { showOfficialSourcesDialog = false }
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "Sabhi Dekhein",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(onClick = onSeeAllClick)
                .padding(4.dp)
        )
    }
}

@Composable
fun ServiceGridItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NewOpportunityCard(
    title: String,
    subtitle: String,
    badgeText: String,
    matchStatus: String,
    verificationStatus: String = "VERIFIED",
    lastVerifiedDate: String = "",
    onMatchClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandIndigo.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = BrandIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                VerificationBadge(status = verificationStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (lastVerifiedDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "• Verified: $lastVerifiedDate",
                    fontSize = 10.sp,
                    color = Slate700
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val (pillColor, pillText) = when (matchStatus) {
                "STRONG" -> Pair(VerifiedGreen, "🟢 Strong Match")
                "POSSIBLE" -> Pair(Color(0xFFE65100), "🟡 Possible Match")
                else -> Pair(Color.Red, "🔴 Doesn't Match")
            }

            Surface(
                color = pillColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onMatchClick)
            ) {
                Text(
                    text = "$pillText • Why?",
                    color = pillColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TrendingItemRow(
    title: String,
    category: String,
    views: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = BrandIndigo,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$views views",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
