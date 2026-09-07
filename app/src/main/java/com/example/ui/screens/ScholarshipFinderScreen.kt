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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.ScholarshipItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.MatchStatusPill
import com.example.ui.components.OfficialSourceButton
import com.example.ui.components.ShareOpportunityButton
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScholarshipFinderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scholarships by viewModel.scholarships.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val searchQuery by viewModel.scholarshipSearchQuery.collectAsState()
    val categoryFilter by viewModel.scholarshipCategoryFilter.collectAsState()
    val stateFilter by viewModel.scholarshipStateFilter.collectAsState()

    var catExpanded by remember { mutableStateOf(false) }
    val categoriesList = listOf("All", "General", "OBC", "SC/ST", "EWS", "Girls")

    var stateExpanded by remember { mutableStateOf(false) }
    val statesList = listOf("All", "All India", "Delhi")

    val filteredScholarships = scholarships.filter { sch ->
        val matchesQuery = searchQuery.isBlank() ||
                sch.title.contains(searchQuery, ignoreCase = true) ||
                sch.provider.contains(searchQuery, ignoreCase = true) ||
                sch.eligibleCourses.contains(searchQuery, ignoreCase = true)

        val matchesCategory = categoryFilter == "All" ||
                sch.eligibleCategories.contains("All", ignoreCase = true) ||
                sch.eligibleCategories.contains(categoryFilter, ignoreCase = true)

        val matchesState = stateFilter == "All" ||
                sch.state.equals("All India", ignoreCase = true) ||
                sch.state.equals(stateFilter, ignoreCase = true)

        matchesQuery && matchesCategory && matchesState
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "Scholarship Finder",
            subtitle = "${filteredScholarships.size} Verified Schemes",
            onBackClick = { viewModel.navigateBack() }
        )

        // Mandatory Disclaimer Banner
        Surface(
            color = BrandIndigo.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = BrandIndigo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Authentic Govt & Trust criteria only. No agency can guarantee scholarship selection; award is strictly subject to institutional verification.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 15.sp
                )
            }
        }

        // Filters Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.scholarshipSearchQuery.value = it },
                placeholder = { Text("Search by name, course, or provider...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandIndigo) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.scholarshipSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scholarship_search_field")
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Category Filter
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    FilterChip(
                        selected = categoryFilter != "All",
                        onClick = { catExpanded = true },
                        label = { Text("Category: $categoryFilter", fontSize = 11.sp) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categoriesList.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    viewModel.scholarshipCategoryFilter.value = c
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // State Filter
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    FilterChip(
                        selected = stateFilter != "All",
                        onClick = { stateExpanded = true },
                        label = { Text("Region: $stateFilter", fontSize = 11.sp) },
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
                                    viewModel.scholarshipStateFilter.value = s
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Scholarships List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredScholarships) { scholarship ->
                val isSaved = savedItems.any { it.itemId == scholarship.id }
                val match = viewModel.getScholarshipMatch(scholarship)

                ScholarshipItemCard(
                    scholarship = scholarship,
                    isSaved = isSaved,
                    match = match,
                    onSaveToggle = {
                        viewModel.toggleSaveItem("SCHOLARSHIP", scholarship.id, scholarship.title, scholarship.amountPerYear)
                    },
                    onMatchClick = {
                        viewModel.smartMatchDetail.value = Pair(scholarship.title, match)
                    },
                    onReportClick = {
                        viewModel.submitReport("SCHOLARSHIP", scholarship.id, "Information check needed", "User flagged scholarship details")
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
fun ScholarshipItemCard(
    scholarship: ScholarshipItem,
    isSaved: Boolean,
    match: com.example.data.model.MatchResult,
    onSaveToggle: () -> Unit,
    onMatchClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scholarship_card_${scholarship.id}"),
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
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = scholarship.amountPerYear,
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationBadge(status = scholarship.verificationStatus)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_scholarship_${scholarship.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Scholarship",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scholarship.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Offered by: ${scholarship.provider}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "• Category: ${scholarship.eligibleCategories} | Domicile: ${scholarship.state}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "• Annual Family Income Ceiling: ₹${scholarship.maxAnnualIncome / 100000.0} Lakh",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "• Min Marks Required: ${scholarship.minPercentage}% in ${scholarship.qualificationRequired}",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = DangerRed, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Last Date: ${scholarship.deadline}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "• Verified: ${scholarship.lastUpdated}",
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            MatchStatusPill(
                matchResult = match,
                onClick = onMatchClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfficialSourceButton(
                    url = scholarship.officialWebsite,
                    label = "Official Application Link"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShareOpportunityButton(
                        title = scholarship.title,
                        subtitle = "${scholarship.amountPerYear} • Deadline: ${scholarship.deadline}",
                        url = scholarship.officialWebsite,
                        opportunityId = scholarship.id
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
