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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("Rohan Sharma") }
    var selectedState by remember { mutableStateOf("Delhi") }
    var stateExpanded by remember { mutableStateOf(false) }
    val statesList = listOf("Delhi", "Uttar Pradesh", "Bihar", "Rajasthan", "Maharashtra", "Tamil Nadu", "West Bengal", "Karnataka", "Punjab", "Haryana", "Madhya Pradesh", "Other")

    var selectedQual by remember { mutableStateOf("12th Standard") }
    var qualExpanded by remember { mutableStateOf(false) }
    val qualList = listOf("10th Standard", "12th Standard", "Diploma / ITI", "Undergraduate (UG)", "Postgraduate (PG)")

    var selectedStream by remember { mutableStateOf("Science (PCM)") }
    var streamExpanded by remember { mutableStateOf(false) }
    val streamList = listOf("Science (PCM)", "Science (PCB)", "Commerce", "Arts / Humanities", "Vocational")

    var selectedCategory by remember { mutableStateOf("General") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val categoryList = listOf("General", "OBC", "SC/ST", "EWS", "Girls")

    var selectedIncome by remember { mutableStateOf("₹2.5L - ₹8L") }
    var incomeExpanded by remember { mutableStateOf(false) }
    val incomeList = listOf("< ₹1L", "₹1L - ₹2.5L", "₹2.5L - ₹8L", "> ₹8L")

    var marksText by remember { mutableStateOf("82") }
    var marksError by remember { mutableStateOf<String?>(null) }
    var isHostelNeeded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // App Logo & Tagline Header
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(BrandIndigo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = BrandAmber,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Student Help Hub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "“Student ki padhai se career tak, sab ek jagah.”",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Aapka Profile Setup Karein 🎯",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Isse aapko personalized colleges aur scholarships milenge.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_student_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // State Dropdown
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State / Domicile") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        statesList.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    selectedState = s
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Qualification Dropdown
                ExposedDropdownMenuBox(
                    expanded = qualExpanded,
                    onExpandedChange = { qualExpanded = !qualExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedQual,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Qualification") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = qualExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = qualExpanded,
                        onDismissRequest = { qualExpanded = false }
                    ) {
                        qualList.forEach { q ->
                            DropdownMenuItem(
                                text = { Text(q) },
                                onClick = {
                                    selectedQual = q
                                    qualExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stream Dropdown
                ExposedDropdownMenuBox(
                    expanded = streamExpanded,
                    onExpandedChange = { streamExpanded = !streamExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedStream,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Stream / Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = streamExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = streamExpanded,
                        onDismissRequest = { streamExpanded = false }
                    ) {
                        streamList.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    selectedStream = st
                                    streamExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category (Reservation / General)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Annual Family Income Dropdown
                ExposedDropdownMenuBox(
                    expanded = incomeExpanded,
                    onExpandedChange = { incomeExpanded = !incomeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedIncome,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Annual Family Income") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = incomeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = incomeExpanded,
                        onDismissRequest = { incomeExpanded = false }
                    ) {
                        incomeList.forEach { inc ->
                            DropdownMenuItem(
                                text = { Text(inc) },
                                onClick = {
                                    selectedIncome = inc
                                    incomeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Percentage / Marks
                OutlinedTextField(
                    value = marksText,
                    onValueChange = { input ->
                        marksText = input
                        val d = input.toDoubleOrNull()
                        marksError = if (input.isNotBlank() && (d == null || d < 0.0 || d > 100.0)) {
                            "Kripya 0% se 100% ke beech valid percentage darj karein"
                        } else null
                    },
                    label = { Text("Latest Marks / Percentage (%)") },
                    isError = marksError != null,
                    supportingText = {
                        marksError?.let { err ->
                            Text(text = err, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_marks")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isHostelNeeded = !isHostelNeeded }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isHostelNeeded,
                        onCheckedChange = { isHostelNeeded = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Hostel facility zaroori hai (Hostel Required)", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = {
                val d = marksText.toDoubleOrNull()
                if (marksText.isNotBlank() && (d == null || d < 0.0 || d > 100.0)) {
                    marksError = "Kripya 0% se 100% ke beech valid percentage darj karein"
                    return@Button
                }
                val marksVal = d ?: 75.0
                viewModel.saveStudentProfile(
                    StudentProfile(
                        fullName = fullName.ifBlank { "Student" },
                        state = selectedState,
                        qualification = selectedQual,
                        stream = selectedStream,
                        category = selectedCategory,
                        annualIncomeRange = selectedIncome,
                        marksPercentage = marksVal,
                        isHostelNeeded = isHostelNeeded,
                        isGuest = false
                    )
                )
                onComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("onboarding_continue_button")
        ) {
            Text("Aage Badhein (Continue)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Guest Mode / Skip
        OutlinedButton(
            onClick = {
                viewModel.saveStudentProfile(
                    StudentProfile(
                        fullName = "Guest Student",
                        state = "Delhi",
                        qualification = "12th Standard",
                        stream = "Science (PCM)",
                        category = "General",
                        annualIncomeRange = "₹2.5L - ₹8L",
                        marksPercentage = 75.0,
                        isGuest = true
                    )
                )
                onComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_skip_button")
        ) {
            Text("Skip / Continue as Guest (Mehmaan Mode)")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
