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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.CollegeItem
import com.example.data.model.ScholarshipItem
import com.example.ui.components.HubTopBar
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isAdmin by viewModel.isAdminAuthenticated.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    if (!isAdmin) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HubTopBar(
                title = "Admin Authentication",
                subtitle = "Institutional Access Restricted",
                onBackClick = { viewModel.navigateBack() }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Admin Access Locked 🔒",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "This area is strictly restricted to institution admins for verification of colleges, scholarships, and broadcasts. Enter PIN to proceed.",
                            fontSize = 12.sp,
                            color = Slate700,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = {
                                enteredPin = it
                                pinError = false
                            },
                            label = { Text("Enter Admin PIN (Default: 1800)") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = pinError,
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_pin_input_field")
                        )

                        if (pinError) {
                            Text(
                                text = "Invalid Security PIN. Please re-enter.",
                                color = DangerRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val success = viewModel.authenticateAdmin(enteredPin)
                                if (!success) {
                                    pinError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_unlock_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Unlock Admin Dashboard")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { viewModel.navigateBack() }) {
                            Text("Return to Student Portal")
                        }
                    }
                }
            }
        }
        return
    }

    val colleges by viewModel.colleges.collectAsState()
    val scholarships by viewModel.scholarships.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val auditReport by viewModel.dataAuditReport.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Verification Queue", "Manage Colleges", "Manage Scholarships", "Community Reports", "Announcements", "Data Health & Audit")

    var showAddCollegeDialog by remember { mutableStateOf(false) }
    var showAddScholarshipDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "Admin Dashboard",
            subtitle = "Opportunity Verification & Content Moderation",
            onBackClick = { viewModel.navigateBack() }
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Verification Queue
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Verification Queue Standards", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Admins must verify official gazette/university portals before approving any opportunity.", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    items(scholarships) { sch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "SCHOLARSHIP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                                    VerificationBadge(status = sch.verificationStatus)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = sch.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Provider: ${sch.provider} • Portal: ${sch.officialWebsite}", fontSize = 11.sp, color = Slate700)

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.adminUpdateScholarshipVerification(sch.id, "VERIFIED") },
                                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verify", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.adminUpdateScholarshipVerification(sch.id, "NEEDS VERIFICATION") },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber)
                                    ) {
                                        Text("Flag", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }

            1 -> {
                // Manage Colleges
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddCollegeDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New College")
                        }
                    }

                    items(colleges) { col ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = col.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "${col.city}, ${col.state} • ${col.annualFees}", fontSize = 11.sp, color = Slate700)
                                }
                                IconButton(onClick = { viewModel.adminDeleteCollege(col.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }

            2 -> {
                // Manage Scholarships
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddScholarshipDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Scholarship")
                        }
                    }

                    items(scholarships) { sch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = sch.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "${sch.provider} • ${sch.amountPerYear}", fontSize = 11.sp, color = Slate700)
                                }
                                IconButton(onClick = { viewModel.adminDeleteScholarship(sch.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }

            3 -> {
                // Community Reports
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (reports.isEmpty()) {
                        item {
                            Text("No reports pending moderation. Community is clean and helpful! ✨", color = Slate700, fontSize = 13.sp)
                        }
                    } else {
                        items(reports) { rep ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "REPORTED: ${rep.targetType} #${rep.targetId}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DangerRed)
                                        Text(text = rep.status, fontSize = 11.sp, color = Slate700)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Reason: ${rep.reason}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    if (rep.details.isNotBlank()) {
                                        Text(text = "Details: ${rep.details}", fontSize = 11.sp, color = Slate700)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { viewModel.adminResolveReport(rep.id, "DISMISSED") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text("Dismiss", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.adminResolveReport(rep.id, "RESOLVED_DELETED") },
                                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                                        ) {
                                            Text("Take Down", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }

            4 -> {
                // Announcements Broadcast
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showBroadcastDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Broadcast New Notice")
                        }
                    }

                    items(announcements) { ann ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = ann.content, fontSize = 12.sp, color = Slate700)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Date: ${ann.date}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }

            5 -> {
                // Data Health & Audit Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Opportunity Data Health & Integrity",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Automated integrity verification for duplicates, broken URLs, expired deadlines, and gazette compliance.",
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.runDataHealthAudit() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("run_audit_button")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Run Fresh Integrity Audit")
                                }
                            }
                        }
                    }

                    if (auditReport != null) {
                        val report = auditReport!!
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Audit Summary Results", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Overall Authenticity Score:", fontSize = 13.sp)
                                        Text("${report.verifiedPercentage}% Verified 🟢", fontWeight = FontWeight.Bold, color = VerifiedGreen)
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Verified Colleges:", fontSize = 13.sp)
                                        Text("${report.totalColleges}", fontWeight = FontWeight.Bold)
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Scholarships Monitored:", fontSize = 13.sp)
                                        Text("${report.totalScholarships}", fontWeight = FontWeight.Bold)
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Entrance Exams Tracked:", fontSize = 13.sp)
                                        Text("${report.totalExams}", fontWeight = FontWeight.Bold)
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Duplicate IDs Detected:", fontSize = 13.sp)
                                        Text(
                                            text = if (report.duplicateIdCount == 0) "0 (Passed ✅)" else "${report.duplicateIdCount} ⚠️",
                                            fontWeight = FontWeight.Bold,
                                            color = if (report.duplicateIdCount == 0) VerifiedGreen else DangerRed
                                        )
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Missing Official URLs:", fontSize = 13.sp)
                                        Text(
                                            text = if (report.missingUrlCount == 0) "0 (Passed ✅)" else "${report.missingUrlCount} ⚠️",
                                            fontWeight = FontWeight.Bold,
                                            color = if (report.missingUrlCount == 0) VerifiedGreen else DangerRed
                                        )
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Expired Deadlines in Catalog:", fontSize = 13.sp)
                                        Text(
                                            text = "${report.expiredDeadlinesCount} Expired",
                                            fontWeight = FontWeight.Bold,
                                            color = if (report.expiredDeadlinesCount == 0) VerifiedGreen else BrandAmber
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No Audit Run Yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "Tap 'Run Fresh Integrity Audit' above to scan all colleges, schemes, exams and deadlines.",
                                        fontSize = 12.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }

    // Add College Dialog
    if (showAddCollegeDialog) {
        AddCollegeModalDialog(
            onDismiss = { showAddCollegeDialog = false },
            onAdd = { col ->
                viewModel.adminAddCollege(col)
                showAddCollegeDialog = false
            }
        )
    }

    // Add Scholarship Dialog
    if (showAddScholarshipDialog) {
        AddScholarshipModalDialog(
            onDismiss = { showAddScholarshipDialog = false },
            onAdd = { sch ->
                viewModel.adminAddScholarship(sch)
                showAddScholarshipDialog = false
            }
        )
    }

    // Broadcast Notice Dialog
    if (showBroadcastDialog) {
        BroadcastNoticeModalDialog(
            onDismiss = { showBroadcastDialog = false },
            onBroadcast = { title, content, isImp ->
                viewModel.adminBroadcastAnnouncement(title, content, isImp)
                showBroadcastDialog = false
            }
        )
    }
}

@Composable
fun AddCollegeModalDialog(
    onDismiss: () -> Unit,
    onAdd: (CollegeItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var univ by remember { mutableStateOf("State University") }
    var state by remember { mutableStateOf("Delhi") }
    var city by remember { mutableStateOf("New Delhi") }
    var courses by remember { mutableStateOf("B.Tech, B.Sc, B.A") }
    var fees by remember { mutableStateOf("₹30,000 / year") }
    var isGovt by remember { mutableStateOf(true) }
    var hostel by remember { mutableStateOf(true) }
    var website by remember { mutableStateOf("https://") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add College to Hub", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("College Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = fees, onValueChange = { fees = it }, label = { Text("Annual Fees") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("Official Website") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            CollegeItem(
                                id = "col_${System.currentTimeMillis()}",
                                name = name,
                                university = univ,
                                state = state,
                                city = city,
                                coursesOffered = courses,
                                annualFees = fees,
                                isGovernment = isGovt,
                                hostelAvailable = hostel,
                                officialWebsite = website,
                                rankingInfo = "Verified Listing",
                                cutoffSummary = "Merit Based",
                                verificationStatus = "VERIFIED"
                            )
                        )
                    }
                }
            ) { Text("Save College") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddScholarshipModalDialog(
    onDismiss: () -> Unit,
    onAdd: (ScholarshipItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("Govt / Trust") }
    var amount by remember { mutableStateOf("₹25,000 / year") }
    var deadline by remember { mutableStateOf("2026-11-30") }
    var website by remember { mutableStateOf("https://scholarships.gov.in") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Scholarship Scheme", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Scholarship Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = provider, onValueChange = { provider = it }, label = { Text("Provider Agency") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Grant Amount") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline (YYYY-MM-DD)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("Official Portal URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            ScholarshipItem(
                                id = "sch_${System.currentTimeMillis()}",
                                title = title,
                                provider = provider,
                                state = "All India",
                                eligibleCourses = "All UG/PG",
                                eligibleCategories = "All",
                                maxAnnualIncome = 400000,
                                minPercentage = 60.0,
                                qualificationRequired = "12th Passed",
                                amountPerYear = amount,
                                deadline = deadline,
                                officialWebsite = website,
                                verificationStatus = "VERIFIED"
                            )
                        )
                    }
                }
            ) { Text("Save Scholarship") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun BroadcastNoticeModalDialog(
    onDismiss: () -> Unit,
    onBroadcast: (String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Broadcast Public Notice", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notice Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Announcement Details") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onBroadcast(title, content, true)
                    }
                }
            ) { Text("Broadcast") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
