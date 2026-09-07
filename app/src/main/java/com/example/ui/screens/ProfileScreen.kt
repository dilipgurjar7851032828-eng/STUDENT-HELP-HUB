package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.ui.components.HubTopBar
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.Slate700
import com.example.ui.theme.VerifiedGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showConfirmWipeDialog by remember { mutableStateOf(false) }
    var showAdminPinDialog by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }
    var adminPinError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        HubTopBar(
            title = "Student Profile & Settings",
            subtitle = "Manage preferences & achievements",
            onBackClick = { viewModel.navigateBack() }
        )

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BrandIndigo,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = profile?.fullName?.ifBlank { "Guest Student" } ?: "Guest Student",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${profile?.qualification} • ${profile?.category}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "State: ${profile?.state}",
                                fontSize = 12.sp,
                                color = BrandIndigo,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = BrandIndigo)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Marks", fontSize = 11.sp, color = Slate700)
                            Text(text = "${profile?.marksPercentage}%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Income", fontSize = 11.sp, color = Slate700)
                            Text(text = profile?.annualIncomeRange ?: "-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Stream", fontSize = 11.sp, color = Slate700)
                            Text(text = profile?.stream?.take(11) ?: "-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Achievements & Badges
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Student Badges & Achievements 🏆", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                val unlocked = profile?.badgesUnlocked?.split(",")?.toSet() ?: emptySet()
                val allBadges = listOf(
                    Pair("PROFILE_COMPLETED", "Profile Pro 🎯"),
                    Pair("FIRST_SAVE", "Opportunity Saver 🔖"),
                    Pair("DOCS_MASTER", "Docs Master 📑"),
                    Pair("HELPFUL_PEER", "Helpful Peer 🌟"),
                    Pair("APPLICATION_TRACKER", "Planner 📈")
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allBadges.forEach { (key, label) ->
                        val isAchieved = unlocked.contains(key)
                        Surface(
                            color = if (isAchieved) BrandAmber.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (isAchieved) label else "🔒 $label",
                                color = if (isAchieved) BrandIndigo else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Non-Cash Student Referral & Sharing
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = BrandIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Doston Ko Jodein (Refer a Friend)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Help your classmates discover verified scholarships & deadlines. Earn community recognition badges! (No money / ethical referral)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = profile?.referralCode ?: "SHUB-DEL782",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val code = profile?.referralCode ?: "SHUB-DEL782"
                            val shareText = "🎓 Join Student Help Hub for authentic colleges, scholarships & exam updates!\nUse referral code: $code\nTagline: Student ki padhai se career tak, sab ek jagah."
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share invite via"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Code", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Account System & Session Management Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("account_session_card"),
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
                    Text(text = "Account Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Surface(
                        color = if (profile?.isGuest == true) BrandAmber.copy(alpha = 0.15f) else VerifiedGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (profile?.isGuest == true) "GUEST MODE" else "REGISTERED USER",
                            color = if (profile?.isGuest == true) BrandAmber else VerifiedGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (profile?.isGuest == true) {
                    Text(
                        text = "You are currently using the app in Guest Mode. Your saved opportunities and application tracker are temporary.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_login_signup_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Login or Create Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Text(
                        text = "Logged in as: ${profile?.email?.ifBlank { profile?.fullName } ?: "Active Student"}",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.logoutUser() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_logout_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sign Out / Switch", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_switch_account_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Change Account", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // App Preferences
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "App Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = BrandIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Language Preference", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = profile?.selectedLanguage ?: "Hinglish", fontSize = 11.sp, color = Slate700)
                        }
                    }

                    Row {
                        val langs = listOf("Hinglish", "Hindi", "English")
                        langs.forEach { l ->
                            Surface(
                                color = if (profile?.selectedLanguage == l) BrandIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .clickable {
                                        profile?.let { viewModel.saveStudentProfile(it.copy(selectedLanguage = l)) }
                                    }
                            ) {
                                Text(
                                    text = l,
                                    color = if (profile?.selectedLanguage == l) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Deadline Notifications", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Switch(
                        checked = profile?.notificationsEnabled ?: true,
                        onCheckedChange = { isChecked ->
                            profile?.let { viewModel.saveStudentProfile(it.copy(notificationsEnabled = isChecked)) }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Privacy & Admin Mode
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Privacy & Management", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showConfirmWipeDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = DangerRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Clear Personal Data (Account Reset)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DangerRed)
                        Text(text = "Deletes your saved applications, reminders, and reset profile.", fontSize = 11.sp, color = Slate700)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdminPinDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BrandIndigo)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Admin Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Opportunity verification, moderation & broadcast tool", fontSize = 11.sp, color = Slate700)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && profile != null) {
        EditProfileModalDialog(
            profile = profile!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.saveStudentProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    // Clear Data Confirmation Dialog
    if (showConfirmWipeDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmWipeDialog = false },
            title = { Text("Clear All Personal Data?") },
            text = { Text("This will permanently clear your application tracking, custom reminders, and saved items from this device.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.wipeStudentData()
                        showConfirmWipeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Clear Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmWipeDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Admin PIN dialog
    if (showAdminPinDialog) {
        AlertDialog(
            onDismissRequest = { showAdminPinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Login")
                }
            },
            text = {
                Column {
                    Text("Enter administrator PIN (Default PIN: 1800):", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = {
                            adminPinInput = it
                            adminPinError = false
                        },
                        singleLine = true,
                        label = { Text("Admin PIN") },
                        isError = adminPinError,
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                    )
                    if (adminPinError) {
                        Text("Incorrect PIN. Please try again.", color = DangerRed, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.authenticateAdmin(adminPinInput)) {
                            showAdminPinDialog = false
                            viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                        } else {
                            adminPinError = true
                        }
                    },
                    modifier = Modifier.testTag("admin_pin_submit")
                ) {
                    Text("Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminPinDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileModalDialog(
    profile: StudentProfile,
    onDismiss: () -> Unit,
    onSave: (StudentProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.fullName) }
    var state by remember { mutableStateOf(profile.state) }
    var qualification by remember { mutableStateOf(profile.qualification) }
    var category by remember { mutableStateOf(profile.category) }
    var marks by remember { mutableStateOf(profile.marksPercentage.toString()) }
    var marksError by remember { mutableStateOf<String?>(null) }
    var income by remember { mutableStateOf(profile.annualIncomeRange) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Student Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State / Domicile") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = qualification,
                    onValueChange = { qualification = it },
                    label = { Text("Qualification (10th/12th/UG)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (General/OBC/SC/ST/EWS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = marks,
                    onValueChange = { input ->
                        marks = input
                        val d = input.toDoubleOrNull()
                        marksError = if (input.isNotBlank() && (d == null || d < 0.0 || d > 100.0)) {
                            "0 se 100 ke beech valid percentage darj karein"
                        } else null
                    },
                    label = { Text("Marks / Percentage (%)") },
                    isError = marksError != null,
                    supportingText = {
                        marksError?.let { err ->
                            Text(text = err, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val d = marks.toDoubleOrNull()
                    if (marks.isNotBlank() && (d == null || d < 0.0 || d > 100.0)) {
                        marksError = "0 se 100 ke beech valid percentage darj karein"
                        return@Button
                    }
                    val marksD = d ?: profile.marksPercentage
                    onSave(
                        profile.copy(
                            fullName = name.ifBlank { "Student" },
                            state = state,
                            qualification = qualification,
                            category = category,
                            marksPercentage = marksD
                        )
                    )
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
