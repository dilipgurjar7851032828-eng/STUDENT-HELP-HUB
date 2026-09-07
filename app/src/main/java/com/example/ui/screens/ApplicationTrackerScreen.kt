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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.ApplicationItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.OfficialSourceButton
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val applications by viewModel.applications.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val statusFilter by viewModel.appStatusFilter.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportFromSavedDialog by remember { mutableStateOf(false) }
    var selectedAppForEdit by remember { mutableStateOf<ApplicationItem?>(null) }

    val statusList = listOf("ALL", "PLANNING", "APPLIED", "UNDER_REVIEW", "COMPLETED", "REJECTED", "EXPIRED")

    val filteredApps = applications.filter { app ->
        statusFilter == "ALL" || app.status.equals(statusFilter, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HubTopBar(
                title = "Application Tracker",
                subtitle = "${applications.size} Applications Monitored",
                onBackClick = { viewModel.navigateBack() }
            )

            // Save + Application Tracker Bridge Banner
            if (savedItems.isNotEmpty()) {
                Surface(
                    color = BrandIndigo.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = BrandIndigo,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${savedItems.size} Saved Bookmarks available",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandIndigo
                            )
                        }
                        OutlinedButton(
                            onClick = { showImportFromSavedDialog = true },
                            modifier = Modifier.testTag("add_from_saved_button")
                        ) {
                            Text("Add from Saved 🔖", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Status Filter Chips
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusList.forEach { st ->
                        FilterChip(
                            selected = statusFilter == st,
                            onClick = { viewModel.appStatusFilter.value = st },
                            label = {
                                Text(
                                    text = st.replace("_", " "),
                                    fontSize = 11.sp,
                                    fontWeight = if (statusFilter == st) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // Applications List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredApps.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Timeline, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Applications in this status", fontWeight = FontWeight.Bold)
                                Text("Tap the + button to add and track a new college, scholarship, or exam application.", fontSize = 12.sp, color = Slate700)
                            }
                        }
                    }
                } else {
                    items(filteredApps) { app ->
                        ApplicationCard(
                            app = app,
                            onEditStatus = { selectedAppForEdit = app },
                            onDelete = { viewModel.deleteApplication(app.id) },
                            onScheduleReminders = {
                                viewModel.scheduleMultiStageDeadlineReminders(app.title, app.deadlineDate, app.category)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Add FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_application_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Application", tint = Color.White)
        }
    }

    if (showAddDialog) {
        AddApplicationDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, cat, target, status, date, notes, link ->
                viewModel.addApplication(title, cat, target, status, date, notes, link)
                showAddDialog = false
            }
        )
    }

    if (selectedAppForEdit != null) {
        EditStatusDialog(
            app = selectedAppForEdit!!,
            onDismiss = { selectedAppForEdit = null },
            onUpdate = { newStatus ->
                viewModel.updateApplicationStatus(selectedAppForEdit!!, newStatus)
                selectedAppForEdit = null
            }
        )
    }

    if (showImportFromSavedDialog) {
        ImportFromSavedDialog(
            savedItems = savedItems,
            onImport = { savedItem ->
                viewModel.trackOpportunityFromSaved(savedItem)
                showImportFromSavedDialog = false
            },
            onDismiss = { showImportFromSavedDialog = false }
        )
    }
}

@Composable
fun ApplicationCard(
    app: ApplicationItem,
    onEditStatus: () -> Unit,
    onDelete: () -> Unit,
    onScheduleReminders: () -> Unit
) {
    val (statusBg, statusTextColor) = when (app.status.uppercase()) {
        "PLANNING" -> Pair(BrandAmber.copy(alpha = 0.15f), BrandAmber)
        "APPLIED" -> Pair(BrandIndigo.copy(alpha = 0.15f), BrandIndigo)
        "UNDER_REVIEW" -> Pair(Color(0xFFE0F7FA), Color(0xFF00838F))
        "COMPLETED" -> Pair(Color(0xFFE8F5E9), VerifiedGreen)
        "REJECTED" -> Pair(Color(0xFFFFEBEE), DangerRed)
        else -> Pair(Color(0xFFEEEEEE), Slate700)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_tracker_card_${app.id}"),
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
                    color = statusBg,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable(onClick = onEditStatus)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = app.status.replace("_", " "),
                            color = statusTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Edit, contentDescription = "Edit Status", tint = statusTextColor, modifier = Modifier.size(11.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = app.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Target: ${app.targetName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Deadline: ${app.deadlineDate}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (app.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${app.notes}",
                        fontSize = 11.sp,
                        color = Slate700,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (app.portalLink.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                OfficialSourceButton(
                    url = app.portalLink,
                    label = "Open Application Portal",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4-Stage Deadline Alerts Action
            OutlinedButton(
                onClick = onScheduleReminders,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_schedule_alerts_${app.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = BrandAmber,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Set 4-Stage Alerts (30d, 7d, 1d, Day) ⏰", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApplicationDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("COLLEGE") }
    var catExpanded by remember { mutableStateOf(false) }
    val categories = listOf("COLLEGE", "SCHOLARSHIP", "EXAM")

    var target by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PLANNING") }
    var statusExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("PLANNING", "APPLIED", "UNDER_REVIEW", "COMPLETED")

    var deadline by remember { mutableStateOf("2026-10-31") }
    var notes by remember { mutableStateOf("") }
    var portalLink by remember { mutableStateOf("https://scholarships.gov.in") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Track New Application", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Application Title") },
                    placeholder = { Text("e.g. DU CSAS B.Com Admission") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("app_title_input")
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = !catExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                            categories.forEach { c ->
                                DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                            statuses.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target College / Exam / Scheme") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = portalLink,
                    onValueChange = { portalLink = it },
                    label = { Text("Portal URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Application Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, category, target.ifBlank { title }, status, deadline, notes, portalLink)
                    }
                },
                modifier = Modifier.testTag("save_application_button")
            ) {
                Text("Track Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditStatusDialog(
    app: ApplicationItem,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    val statuses = listOf("PLANNING", "APPLIED", "UNDER_REVIEW", "COMPLETED", "REJECTED", "EXPIRED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Application Status", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select current stage for \"${app.title}\":")
                Spacer(modifier = Modifier.height(12.dp))
                statuses.forEach { st ->
                    Surface(
                        color = if (app.status == st) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUpdate(st) }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = st.replace("_", " "),
                            fontWeight = if (app.status == st) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ImportFromSavedDialog(
    savedItems: List<com.example.data.model.SavedItem>,
    onImport: (com.example.data.model.SavedItem) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add from Saved Bookmarks 🔖", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select a bookmarked opportunity to monitor in your Application Tracker:",
                    fontSize = 12.sp,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (savedItems.isEmpty()) {
                    Text(
                        text = "No saved bookmarks found. Save colleges or scholarships first!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedItems) { saved ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onImport(saved) }
                                    .testTag("import_saved_item_${saved.itemId}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = saved.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${saved.itemType} • ${saved.subtitle}",
                                            fontSize = 11.sp,
                                            color = Slate700
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = BrandIndigo,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Track",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
