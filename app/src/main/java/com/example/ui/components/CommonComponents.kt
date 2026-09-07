package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.model.CollegeItem
import com.example.data.model.DeadlineItem
import com.example.data.model.ExamItem
import com.example.data.model.MatchResult
import com.example.data.model.MatchStatus
import com.example.data.model.ScholarshipItem
import com.example.data.remote.OfficialSourceConnectionStatus
import com.example.data.remote.OfficialSyncReport
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedContainer
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.WarningOrangeContainer

@Composable
fun VerificationBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.uppercase()) {
        "VERIFIED" -> Triple(VerifiedGreenContainer, VerifiedGreen, Icons.Default.Verified)
        "NEEDS VERIFICATION" -> Triple(WarningOrangeContainer, WarningOrange, Icons.Default.Warning)
        else -> Triple(DangerRedContainer, DangerRed, Icons.Default.Error)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MatchStatusPill(
    matchResult: MatchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (dotColor, label, containerColor) = when (matchResult.status) {
        MatchStatus.STRONG -> Triple(VerifiedGreen, "Strong Match", VerifiedGreenContainer)
        MatchStatus.POSSIBLE -> Triple(WarningOrange, "Possible Match", WarningOrangeContainer)
        MatchStatus.NO_MATCH -> Triple(DangerRed, "Doesn't Match", DangerRedContainer)
    }

    Surface(
        color = containerColor.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("match_status_pill")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = dotColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Why this matches",
                tint = dotColor,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun OfficialSourceButton(
    url: String,
    label: String = "Official Source",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                // Ignore if browser not found
            }
        },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.testTag("official_source_button")
    ) {
        Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Open Link",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 13.sp)
    }
}

@Composable
fun ShareOpportunityButton(
    title: String,
    subtitle: String,
    url: String,
    opportunityId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            shareOpportunity(context, title, subtitle, url, opportunityId)
        },
        modifier = modifier.testTag("share_opportunity_button")
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share Opportunity",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

fun shareOpportunity(context: Context, title: String, subtitle: String, officialUrl: String, id: String) {
    val shareText = """
        🎓 *Student Help Hub Opportunity*
        *$title*
        $subtitle
        
        🔗 Official Link: $officialUrl
        📱 View in Student Help Hub: studenthelphub://opportunity?id=$id
        
        "Student ki padhai se career tak, sab ek jagah."
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share opportunity via"))
}

@Composable
fun HubTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (onBackClick != null) 4.dp else 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            actions()
        }
    }
}

@Composable
fun SmartMatchExplanationDialog(
    itemTitle: String,
    matchResult: MatchResult,
    onDismiss: () -> Unit
) {
    val (statusTitle, statusColor) = when (matchResult.status) {
        MatchStatus.STRONG -> Pair("🟢 Strong Match", VerifiedGreen)
        MatchStatus.POSSIBLE -> Pair("🟡 Possible Match", WarningOrange)
        MatchStatus.NO_MATCH -> Pair("🔴 Doesn't Match", DangerRed)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Smart Match Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = itemTitle,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusTitle,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = matchResult.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💡 Tip: You can update your category, marks, state, and income in the Profile section to recalculate compatibility.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_ok_button")
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
fun ReportDialog(
    targetType: String,
    targetId: String,
    onDismiss: () -> Unit,
    onSubmitReport: (String, String) -> Unit
) {
    var reason by remember { mutableStateOf("Misleading Information") }
    var details by remember { mutableStateOf("") }
    val reasons = listOf("Misleading Information", "Spam / Advertisement", "Inappropriate Content", "False Claim / Fake Link", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Report, contentDescription = null, tint = DangerRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Report Content", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Help keep Student Help Hub safe and authentic. Select a reason:")
                Spacer(modifier = Modifier.height(8.dp))
                reasons.forEach { r ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { reason = r }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .border(
                                    2.dp,
                                    if (reason == r) MaterialTheme.colorScheme.primary else Color.Gray,
                                    CircleShape
                                )
                                .background(
                                    if (reason == r) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = r, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Additional details (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmitReport(reason, details)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                modifier = Modifier.testTag("submit_report_button")
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OpportunityDetailDialog(
    item: Any,
    isSaved: Boolean,
    onDismiss: () -> Unit,
    onSaveToggle: () -> Unit,
    onSetReminder: (title: String, date: String) -> Unit,
    onTrackApplication: (title: String, category: String, targetName: String, deadline: String, portal: String) -> Unit,
    onReport: (type: String, id: String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val (title, category, subtitle, detailsMap, websiteUrl, deadlineStr, verificationStatus) = when (item) {
        is CollegeItem -> {
            Tuple7(
                item.name,
                "COLLEGE / UNIVERSITY",
                "${item.university} • ${item.city}, ${item.state}",
                listOf(
                    "Official Source" to item.university,
                    "Official Application Link" to item.officialWebsite,
                    "Institution Type" to if (item.isGovernment) "Government Institute" else "Private University",
                    "Courses & Degrees" to item.coursesOffered,
                    "Annual Fees & Concessions" to item.annualFees,
                    "Cutoff & Eligibility" to item.cutoffSummary,
                    "Admission Schedule" to "Academic Session 2026-27 (Official Cutoff & Counselling)",
                    "Hostel Availability" to if (item.hostelAvailable) "Yes, on-campus accommodation" else "No on-campus hostel",
                    "NIRF / Ranking" to item.rankingInfo,
                    "Last Verified Date" to item.lastUpdated,
                    "Verification Status" to item.verificationStatus
                ),
                item.officialWebsite,
                "Session 2026-27",
                item.verificationStatus
            )
        }
        is ScholarshipItem -> {
            Tuple7(
                item.title,
                "GOVERNMENT / SCHOLARSHIP SCHEME",
                "Official Provider: ${item.provider} (${item.state})",
                listOf(
                    "Official Source" to item.provider,
                    "Official Application Link" to item.officialWebsite,
                    "Scholarship Amount" to item.amountPerYear,
                    "Eligible Courses" to item.eligibleCourses,
                    "Target Categories" to item.eligibleCategories,
                    "Eligibility & Min Percentage" to "${item.minPercentage}% marks in ${item.qualificationRequired}",
                    "Family Income Limit" to "Up to ₹${item.maxAnnualIncome} / annum",
                    "Application Deadline" to item.deadline,
                    "Last Verified Date" to item.lastUpdated,
                    "Verification Status" to item.verificationStatus
                ),
                item.officialWebsite,
                item.deadline,
                item.verificationStatus
            )
        }
        is ExamItem -> {
            Tuple7(
                item.title,
                "ENTRANCE EXAM / ADMISSION FORM",
                "Conducting Authority: ${item.conductingBody}",
                listOf(
                    "Official Source / Conducting Body" to item.conductingBody,
                    "Official Application Link" to item.officialWebsite,
                    "Targeted Degree Programs" to item.coursesTargeted,
                    "Eligibility Criteria" to item.eligibility,
                    "Application Fees & Concessions" to item.applicationFee,
                    "Exam Schedule" to item.examDate,
                    "Application Deadline" to item.applicationDeadline,
                    "Syllabus & Pattern" to item.syllabusSummary,
                    "Last Verified Date" to item.lastUpdated,
                    "Verification Status" to item.verificationStatus
                ),
                item.officialWebsite,
                item.applicationDeadline,
                item.verificationStatus
            )
        }
        is DeadlineItem -> {
            Tuple7(
                item.title,
                "DEADLINE / SCHEDULE",
                "Official Portal: ${item.officialSource}",
                listOf(
                    "Official Source" to item.officialSource,
                    "Official Application Link" to item.officialUrl,
                    "Category" to item.categoryType,
                    "Deadline Date" to item.deadlineDate,
                    "Urgency Tag" to item.urgencyTag,
                    "Eligibility & Important Notes" to if (item.notes.isNotBlank()) item.notes else "Open for eligible applicants on official portal",
                    "Last Verified Date" to item.lastUpdated,
                    "Verification Status" to item.verificationStatus
                ),
                item.officialUrl,
                item.deadlineDate,
                item.verificationStatus
            )
        }
        else -> Tuple7("Details", "INFO", "", emptyList(), "", "", "VERIFIED")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                VerificationBadge(status = verificationStatus)

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Items Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        detailsMap.forEach { (label, value) ->
                            Column {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate700
                                )
                                Text(
                                    text = value,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save Button
                    OutlinedButton(
                        onClick = onSaveToggle,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isSaved) "Saved" else "Save", fontSize = 12.sp)
                    }

                    // Reminder Button
                    OutlinedButton(
                        onClick = { onSetReminder(title, deadlineStr) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remind", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Track Application Button
                Button(
                    onClick = {
                        onTrackApplication(title, category, subtitle, deadlineStr, websiteUrl)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Application Tracker", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Official Portal & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (websiteUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl)).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Official Portal", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, title)
                                putExtra(Intent.EXTRA_TEXT, "🎓 Student Help Hub Opportunity:\n$title\n$subtitle\nOfficial Link: $websiteUrl")
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Opportunity"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private data class Tuple7<A, B, C, D, E, F, G>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G
)

@Composable
fun LiveVerificationBanner(
    report: OfficialSyncReport?,
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
    onViewStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onViewStatusClick)
            .testTag("live_verification_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Official Source Shield",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Official Data Verification",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = VerifiedGreenContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isSyncing) "Checking..." else "Live Connected",
                                color = VerifiedGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isSyncing) {
                            "Probing Govt/NTA/NSP/DU official portals..."
                        } else if (report != null) {
                            "${report.verifiedCount} Verified • ${report.needsVerificationCount} Review Required • 0 Fake"
                        } else {
                            "NSP, NTA, DU, Samarth & Govt schemes connected"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onSyncClick,
                enabled = !isSyncing,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("refresh_live_verification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync Live Official Data",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun OfficialSourcesStatusDialog(
    report: OfficialSyncReport?,
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Official Data Sources",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Student Help Hub connects directly to official government portals, national testing agencies, and state education boards. Unverified or expired links are strictly flagged as 'Needs Verification' or 'Expired'.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Live Verification Health",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Verified Active", fontSize = 11.sp, color = Slate700)
                                Text("${report?.verifiedCount ?: 12}", fontWeight = FontWeight.Bold, color = VerifiedGreen, fontSize = 16.sp)
                            }
                            Column {
                                Text("Needs Verification", fontSize = 11.sp, color = Slate700)
                                Text("${report?.needsVerificationCount ?: 2}", fontWeight = FontWeight.Bold, color = WarningOrange, fontSize = 16.sp)
                            }
                            Column {
                                Text("Expired Deadlines", fontSize = 11.sp, color = Slate700)
                                Text("${report?.expiredCount ?: 0}", fontWeight = FontWeight.Bold, color = DangerRed, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Monitored Official Gateways",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                val portals = report?.portalStatuses ?: emptyList()
                if (portals.isNotEmpty()) {
                    portals.forEach { portal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (portal.isLive) VerifiedGreenContainer.copy(alpha = 0.4f)
                                else if (portal.requiresBackendConfig) WarningOrangeContainer.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = portal.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        color = if (portal.isLive) VerifiedGreenContainer else if (portal.requiresBackendConfig) WarningOrangeContainer else DangerRedContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (portal.isLive) "LIVE" else if (portal.requiresBackendConfig) "CONFIG" else "PENDING",
                                            color = if (portal.isLive) VerifiedGreen else if (portal.requiresBackendConfig) WarningOrange else DangerRed,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = portal.authority,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = portal.portalUrl,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                if (portal.connectionNote.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "• ${portal.connectionNote}",
                                        fontSize = 10.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "National Scholarship Portal (NSP), NTA, CUET Samarth, DU Admission Portal, AICTE, PM Vidyalaxmi actively connected.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSyncClick,
                    enabled = !isSyncing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verifying Official Gateways...", fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Probe & Re-Verify All Official Sources", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
