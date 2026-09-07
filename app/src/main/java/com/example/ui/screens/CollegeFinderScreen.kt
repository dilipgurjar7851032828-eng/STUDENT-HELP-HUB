package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollegeItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.OfficialSourceButton
import com.example.ui.components.ShareOpportunityButton
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CollegeFinderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val colleges by viewModel.colleges.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val searchQuery by viewModel.collegeSearchQuery.collectAsState()
    val stateFilter by viewModel.collegeStateFilter.collectAsState()
    val govtFilter by viewModel.collegeGovtFilter.collectAsState()
    val hostelFilter by viewModel.collegeHostelFilter.collectAsState()

    var stateExpanded by remember { mutableStateOf(false) }
    val statesList = listOf("All", "Delhi", "Tamil Nadu", "West Bengal", "Uttar Pradesh", "Rajasthan")

    val filteredColleges = colleges.filter { col ->
        val matchesQuery = searchQuery.isBlank() ||
                col.name.contains(searchQuery, ignoreCase = true) ||
                col.city.contains(searchQuery, ignoreCase = true) ||
                col.coursesOffered.contains(searchQuery, ignoreCase = true) ||
                col.university.contains(searchQuery, ignoreCase = true)

        val matchesState = stateFilter == "All" || col.state.equals(stateFilter, ignoreCase = true)
        val matchesGovt = govtFilter == null || col.isGovernment == govtFilter
        val matchesHostel = !hostelFilter || col.hostelAvailable

        matchesQuery && matchesState && matchesGovt && matchesHostel
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "College Finder",
            subtitle = "${filteredColleges.size} Institutes Found",
            onBackClick = { viewModel.navigateBack() }
        )

        // Filter Header Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.collegeSearchQuery.value = it },
                placeholder = { Text("Search college, course (B.Tech, B.Com), city...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandIndigo) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.collegeSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("college_search_field")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips FlowRow
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // State selector
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    FilterChip(
                        selected = stateFilter != "All",
                        onClick = { stateExpanded = true },
                        label = { Text("State: $stateFilter", fontSize = 11.sp) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        statesList.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    viewModel.collegeStateFilter.value = s
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }

                // Govt filter chip
                FilterChip(
                    selected = govtFilter == true,
                    onClick = {
                        viewModel.collegeGovtFilter.value = if (govtFilter == true) null else true
                    },
                    label = { Text("Government Only", fontSize = 11.sp) },
                    leadingIcon = if (govtFilter == true) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )

                // Private filter chip
                FilterChip(
                    selected = govtFilter == false,
                    onClick = {
                        viewModel.collegeGovtFilter.value = if (govtFilter == false) null else false
                    },
                    label = { Text("Private Only", fontSize = 11.sp) },
                    leadingIcon = if (govtFilter == false) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )

                // Hostel filter chip
                FilterChip(
                    selected = hostelFilter,
                    onClick = { viewModel.collegeHostelFilter.value = !hostelFilter },
                    label = { Text("Hostel Available", fontSize = 11.sp) },
                    leadingIcon = if (hostelFilter) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )
            }
        }

        // College List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredColleges) { college ->
                val isSaved = savedItems.any { it.itemId == college.id }
                val match = viewModel.getCollegeMatch(college)

                CollegeItemCard(
                    college = college,
                    isSaved = isSaved,
                    match = match,
                    onSaveToggle = {
                        viewModel.toggleSaveItem("COLLEGE", college.id, college.name, college.city)
                    },
                    onMatchClick = {
                        viewModel.smartMatchDetail.value = Pair(college.name, match)
                    },
                    onReportClick = {
                        viewModel.submitReport("COLLEGE", college.id, "Information check needed", "User flagged college details")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun CollegeItemCard(
    college: CollegeItem,
    isSaved: Boolean,
    match: com.example.data.model.MatchResult,
    onSaveToggle: () -> Unit,
    onMatchClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("college_card_${college.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (college.isGovernment) Color(0xFFE8F5E9) else Color(0xFFEDE7F6),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (college.isGovernment) "GOVERNMENT" else "PRIVATE",
                        color = if (college.isGovernment) Color(0xFF2E7D32) else Color(0xFF512DA8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationBadge(status = college.verificationStatus)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_college_${college.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save College",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = college.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = college.university,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Details info grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Slate700, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${college.city}, ${college.state}", fontSize = 12.sp, color = Slate700)
                }

                if (college.hostelAvailable) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF00897B), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Hostel Available", fontSize = 12.sp, color = Color(0xFF00897B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Courses Offered: ${college.coursesOffered}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Annual Fees: ${college.annualFees}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Expected Cutoff / Admission: ${college.cutoffSummary}",
                        fontSize = 11.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Admission Session: 2026-27 • Last Verified: ${college.lastUpdated}",
                        fontSize = 11.sp,
                        color = Slate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Match Indicator
            MatchStatusPill(
                matchResult = match,
                onClick = onMatchClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfficialSourceButton(
                    url = college.officialWebsite,
                    label = "Official Admission Link"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShareOpportunityButton(
                        title = college.name,
                        subtitle = "${college.city} • ${college.annualFees}",
                        url = college.officialWebsite,
                        opportunityId = college.id
                    )

                    IconButton(
                        onClick = onReportClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "Report item",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
