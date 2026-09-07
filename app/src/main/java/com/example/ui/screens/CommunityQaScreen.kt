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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.CommunityAnswer
import com.example.data.model.CommunityQuestion
import com.example.ui.components.HubTopBar
import com.example.ui.components.ReportDialog
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CommunityQaScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.questions.collectAsState()
    val selectedQuestion by viewModel.selectedQuestionForAnswers.collectAsState()
    val currentAnswers by viewModel.currentQuestionAnswers.collectAsState()

    var showAskDialog by remember { mutableStateOf(false) }
    var reportTarget by remember { mutableStateOf<Pair<String, String>?>(null) }

    if (selectedQuestion != null) {
        // Question Discussion Detail Screen
        QuestionDiscussionDetail(
            question = selectedQuestion!!,
            answers = currentAnswers,
            onBack = { viewModel.selectedQuestionForAnswers.value = null },
            onUpvoteQuestion = { viewModel.upvoteQuestion(selectedQuestion!!.id) },
            onReportQuestion = {
                reportTarget = Pair("QUESTION", selectedQuestion!!.id.toString())
            },
            onPostAnswer = { text ->
                viewModel.postAnswer(selectedQuestion!!.id, text)
            },
            onUpvoteAnswer = { answerId ->
                viewModel.upvoteAnswer(answerId)
            },
            onReportAnswer = { answerId ->
                reportTarget = Pair("ANSWER", answerId.toString())
            }
        )
    } else {
        // Questions Feed List
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HubTopBar(
                    title = "Community Q&A",
                    subtitle = "Ask doubts & get help from verified seniors",
                    onBackClick = { viewModel.navigateBack() }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(questions) { q ->
                        QuestionCard(
                            question = q,
                            onClick = { viewModel.selectQuestionForAnswers(q) },
                            onUpvote = { viewModel.upvoteQuestion(q.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            // Ask FAB
            FloatingActionButton(
                onClick = { showAskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("ask_question_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ask Question", tint = Color.White)
            }
        }
    }

    if (showAskDialog) {
        AskQuestionDialog(
            onDismiss = { showAskDialog = false },
            onPost = { title, body, tag ->
                viewModel.askQuestion(title, body, tag)
                showAskDialog = false
            }
        )
    }

    if (reportTarget != null) {
        ReportDialog(
            targetType = reportTarget!!.first,
            targetId = reportTarget!!.second,
            onDismiss = { reportTarget = null },
            onSubmitReport = { reason, details ->
                viewModel.submitReport(reportTarget!!.first, reportTarget!!.second, reason, details)
                reportTarget = null
            }
        )
    }
}

@Composable
fun QuestionCard(
    question: CommunityQuestion,
    onClick: () -> Unit,
    onUpvote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("question_card_${question.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
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
                        text = question.tag,
                        color = BrandIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                if (question.isResolved) {
                    Surface(
                        color = VerifiedGreenContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "RESOLVED ✅",
                            color = VerifiedGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = question.body,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asked by ${question.authorName} (${question.authorQualification})",
                    fontSize = 11.sp,
                    color = Slate700
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = onUpvote)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "Upvote", tint = BrandIndigo, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${question.upvotes}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandIndigo)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = "Answers", tint = Slate700, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${question.answerCount}", fontSize = 12.sp, color = Slate700)
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionDiscussionDetail(
    question: CommunityQuestion,
    answers: List<CommunityAnswer>,
    onBack: () -> Unit,
    onUpvoteQuestion: () -> Unit,
    onReportQuestion: () -> Unit,
    onPostAnswer: (String) -> Unit,
    onUpvoteAnswer: (Long) -> Unit,
    onReportAnswer: (Long) -> Unit
) {
    var answerInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "Discussion",
            subtitle = question.tag,
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Question Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = question.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.body,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "By ${question.authorName} • ${question.authorQualification}",
                                fontSize = 11.sp,
                                color = Slate700
                            )
                            Row {
                                IconButton(onClick = onUpvoteQuestion, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = onReportQuestion, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Report, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Answers (${answers.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (answers.isEmpty()) {
                item {
                    Text(
                        text = "No answers yet. Be the first senior or peer to answer this student!",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                }
            } else {
                items(answers) { ans ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = ans.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = BrandIndigo.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = ans.authorRole,
                                            color = BrandIndigo,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { onUpvoteAnswer(ans.id) }, modifier = Modifier.size(30.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ThumbUp, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("${ans.upvotes}", fontSize = 11.sp)
                                        }
                                    }
                                    IconButton(onClick = { onReportAnswer(ans.id) }, modifier = Modifier.size(30.dp)) {
                                        Icon(Icons.Default.Report, contentDescription = null, tint = DangerRed, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = ans.body,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Post Answer Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    placeholder = { Text("Write a helpful, verified answer...", fontSize = 13.sp) },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("answer_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (answerInput.isNotBlank()) {
                            onPostAnswer(answerInput.trim())
                            answerInput = ""
                        }
                    },
                    modifier = Modifier.testTag("post_answer_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Post Answer", tint = BrandIndigo)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskQuestionDialog(
    onDismiss: () -> Unit,
    onPost: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("Admissions") }
    var tagExpanded by remember { mutableStateOf(false) }
    val tags = listOf("Admissions", "Scholarships", "Certificates", "Hostel", "Exams", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ask Student Community 💬", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Question Title") },
                    placeholder = { Text("e.g. Kya EWS certificate Central format zaroori hai?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("ask_title_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                ExposedDropdownMenuBox(
                    expanded = tagExpanded,
                    onExpandedChange = { tagExpanded = !tagExpanded }
                ) {
                    OutlinedTextField(
                        value = tag,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tag / Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = tagExpanded, onDismissRequest = { tagExpanded = false }) {
                        tags.forEach { t ->
                            DropdownMenuItem(text = { Text(t) }, onClick = { tag = t; tagExpanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Details / Context") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().testTag("ask_body_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onPost(title, body, tag)
                    }
                },
                modifier = Modifier.testTag("submit_question_button")
            ) {
                Text("Post Question")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
