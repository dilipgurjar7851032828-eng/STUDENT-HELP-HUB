package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.ExamItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.OfficialSourceButton
import com.example.ui.components.ShareOpportunityButton
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdmissionFormsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exams by viewModel.exams.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredExams = exams.filter { ex ->
        searchQuery.isBlank() ||
                ex.title.contains(searchQuery, ignoreCase = true) ||
                ex.conductingBody.contains(searchQuery, ignoreCase = true) ||
                ex.coursesTargeted.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "Admissions & Entrance Exams",
            subtitle = "CUET, JEE, NEET & Central Portals",
            onBackClick = { viewModel.navigateBack() }
        )

        // Search Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search entrance exams (CUET, JEE, NEET, CLAT)...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandIndigo) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("exams_search_input")
            )
        }

        // Exams List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredExams) { exam ->
                val isSaved = savedItems.any { it.itemId == exam.id }

                ExamCard(
                    exam = exam,
                    isSaved = isSaved,
                    onSaveToggle = {
                        viewModel.toggleSaveItem("EXAM", exam.id, exam.title, exam.conductingBody)
                    },
                    onSetReminder = {
                        viewModel.addReminder("Exam Application: ${exam.title}", exam.applicationDeadline, "10:00 AM")
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
fun ExamCard(
    exam: ExamItem,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onSetReminder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_card_${exam.id}"),
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
                    color = BrandIndigo.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = exam.conductingBody,
                        color = BrandIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationBadge(status = exam.verificationStatus)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onSaveToggle, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Exam",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Target Programs: ${exam.coursesTargeted}",
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
                        text = "• Eligibility: ${exam.eligibility}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Exam Window: ${exam.examDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Registration Last Date: ${exam.applicationDeadline}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Syllabus: ${exam.syllabusSummary}",
                        fontSize = 11.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Official Source: ${exam.conductingBody} • Verified: ${exam.lastUpdated}",
                        fontSize = 11.sp,
                        color = Slate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfficialSourceButton(
                    url = exam.officialWebsite,
                    label = "Official Application Link"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSetReminder,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Set Reminder",
                            tint = BrandAmber
                        )
                    }

                    ShareOpportunityButton(
                        title = exam.title,
                        subtitle = "${exam.conductingBody} • Deadline: ${exam.applicationDeadline}",
                        url = exam.officialWebsite,
                        opportunityId = exam.id
                    )
                }
            }
        }
    }
}
