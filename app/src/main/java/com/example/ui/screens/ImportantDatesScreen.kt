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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.data.remote.OfficialSourceVerifier
import com.example.ui.components.VerificationBadge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.DeadlineItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.OfficialSourceButton
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ImportantDatesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val deadlines by viewModel.deadlines.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Deadlines", "This Week", "This Month", "My Reminders")

    var showAddReminderDialog by remember { mutableStateOf(false) }

    val filteredDeadlines = when (selectedTabIndex) {
        1 -> deadlines.filter { it.urgencyTag == "THIS_WEEK" || it.urgencyTag == "TODAY" }
        2 -> deadlines.filter { it.urgencyTag == "THIS_MONTH" || it.urgencyTag == "THIS_WEEK" || it.urgencyTag == "TODAY" }
        else -> deadlines
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HubTopBar(
                title = "Important Dates & Reminders",
                subtitle = "Never miss a deadline or scholarship closing date",
                onBackClick = { viewModel.navigateBack() }
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            if (selectedTabIndex == 3) {
                // My Reminders tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (reminders.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Alarm, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Custom Reminders Set", fontWeight = FontWeight.Bold)
                                    Text("Tap the + button to add a custom deadline reminder.", fontSize = 12.sp, color = Slate700)
                                }
                            }
                        }
                    } else {
                        items(reminders) { rem ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = rem.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Scheduled for: ${rem.targetDate} at ${rem.targetTime}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteReminder(rem.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            } else {
                // Deadlines tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDeadlines) { deadline ->
                        DeadlineCard(
                            deadline = deadline,
                            onToggleReminder = {
                                viewModel.toggleDeadlineReminder(deadline)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }

        // Add Custom Reminder FAB
        FloatingActionButton(
            onClick = { showAddReminderDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_reminder_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Reminder", tint = Color.White)
        }
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onAdd = { title, date, time ->
                viewModel.addReminder(title, date, time)
                showAddReminderDialog = false
            }
        )
    }
}

@Composable
fun DeadlineCard(
    deadline: DeadlineItem,
    onToggleReminder: () -> Unit
) {
    val (urgencyColor, urgencyLabel) = when (deadline.urgencyTag) {
        "TODAY" -> Pair(DangerRed, "Closing Today ⚠️")
        "THIS_WEEK" -> Pair(Color(0xFFE65100), "This Week")
        "THIS_MONTH" -> Pair(BrandAmber, "This Month")
        else -> Pair(VerifiedGreen, "Upcoming")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deadline_card_${deadline.id}"),
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
                Surface(
                    color = urgencyColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = urgencyLabel,
                        color = urgencyColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isExpired = OfficialSourceVerifier.isDeadlineExpired(deadline.deadlineDate)
                    val status = if (isExpired) "EXPIRED" else "VERIFIED"
                    VerificationBadge(status = status)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = deadline.categoryType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = deadline.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (deadline.notes.isNotBlank()) {
                Text(
                    text = deadline.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Official Source: Central/State Education Authority • Verified: ${OfficialSourceVerifier.getTodayDate()}",
                fontSize = 11.sp,
                color = com.example.ui.theme.Slate700
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Deadline: ${deadline.deadlineDate}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onToggleReminder,
                    modifier = Modifier.testTag("toggle_reminder_${deadline.id}")
                ) {
                    Icon(
                        imageVector = if (deadline.isReminderSet) Icons.Default.AlarmOn else Icons.Default.Alarm,
                        contentDescription = "Toggle reminder",
                        tint = if (deadline.isReminderSet) BrandAmber else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OfficialSourceButton(
                url = deadline.officialUrl,
                label = "Official Application Link",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-10-31") }
    var time by remember { mutableStateOf("10:00 AM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Reminder ⏰", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title / Task") },
                    placeholder = { Text("e.g. Upload 12th marksheet on NSP") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_title_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Target Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g. 10:00 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, date, time)
                    }
                },
                modifier = Modifier.testTag("save_reminder_button")
            ) {
                Text("Save Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
