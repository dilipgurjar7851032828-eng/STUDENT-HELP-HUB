package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.model.CollegeItem
import com.example.data.model.ExamItem
import com.example.data.model.ScholarshipItem
import com.example.ui.components.OpportunityDetailDialog
import com.example.ui.components.SmartMatchExplanationDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdmissionFormsScreen
import com.example.ui.screens.ApplicationTrackerScreen
import com.example.ui.screens.CollegeFinderScreen
import com.example.ui.screens.CommunityQaScreen
import com.example.ui.screens.DocumentChecklistScreen
import com.example.ui.screens.HelpCenterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImportantDatesScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MereLiyeDashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedItemsScreen
import com.example.ui.screens.ScholarshipFinderScreen
import com.example.ui.screens.StudyBuddyAiScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.touch.VirtualPhoneKeyHandler
import com.example.ui.touch.VirtualPhoneTouchHandler
import com.example.ui.touch.reliableVirtualTouch
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntentUri(intent?.data)

        setContent {
            val profile by viewModel.profile.collectAsState()
            val isDark = profile?.isDarkMode ?: isSystemInDarkTheme()
            MyApplicationTheme(darkTheme = isDark) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.dispatchTouchEvent(ev)
        if (VirtualPhoneTouchHandler.shouldFilterTouchEvent(this, ev)) {
            // Accidental duplicate touch / jitter filtered out
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (VirtualPhoneKeyHandler.shouldFilterKeyEvent(event)) {
            // Duplicate keyboard / input event filtered out
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentUri(intent.data)
    }

    private fun handleIntentUri(uri: Uri?) {
        if (uri != null) {
            val id = uri.getQueryParameter("id") ?: uri.lastPathSegment
            val type = uri.getQueryParameter("type") ?: "college"
            if (!id.isNullOrBlank()) {
                viewModel.handleDeepLink(id, type)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val smartMatchDetail by viewModel.smartMatchDetail.collectAsState()
    val selectedOpportunity by viewModel.selectedOpportunityForDetail.collectAsState()
    val savedItems by viewModel.savedItems.collectAsState()
    val noticeMessage by viewModel.userNoticeMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(noticeMessage) {
        noticeMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.userNoticeMessage.value = null
        }
    }

    // Back Handler: Go back through stack if not at root
    BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.LOGIN) {
        viewModel.navigateBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .reliableVirtualTouch()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.LOGIN -> LoginScreen(viewModel = viewModel)
                AppScreen.ONBOARDING -> OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = { viewModel.navigateTo(AppScreen.HOME) }
                )
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.MERE_LIYE_DASHBOARD -> MereLiyeDashboardScreen(viewModel = viewModel)
                AppScreen.COLLEGE_FINDER -> CollegeFinderScreen(viewModel = viewModel)
                AppScreen.SCHOLARSHIP_FINDER -> ScholarshipFinderScreen(viewModel = viewModel)
                AppScreen.ADMISSION_FORMS -> AdmissionFormsScreen(viewModel = viewModel)
                AppScreen.IMPORTANT_DATES -> ImportantDatesScreen(viewModel = viewModel)
                AppScreen.DOCUMENT_CHECKLIST -> DocumentChecklistScreen(viewModel = viewModel)
                AppScreen.APPLICATION_TRACKER -> ApplicationTrackerScreen(viewModel = viewModel)
                AppScreen.SAVED_ITEMS -> SavedItemsScreen(viewModel = viewModel)
                AppScreen.STUDY_BUDDY_AI -> StudyBuddyAiScreen(viewModel = viewModel)
                AppScreen.COMMUNITY_QA -> CommunityQaScreen(viewModel = viewModel)
                AppScreen.HELP_CENTER -> HelpCenterScreen(viewModel = viewModel)
                AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
            }

            // Global Smart Match Explanation Dialog
            smartMatchDetail?.let { (title, match) ->
                SmartMatchExplanationDialog(
                    itemTitle = title,
                    matchResult = match,
                    onDismiss = { viewModel.smartMatchDetail.value = null }
                )
            }

            // Global Opportunity Detail Dialog (Triggered by Deep Links, Saved Items, or Search Clicks)
            selectedOpportunity?.let { opp ->
                val oppId = when (opp) {
                    is CollegeItem -> opp.id
                    is ScholarshipItem -> opp.id
                    is ExamItem -> opp.id
                    else -> ""
                }
                val isSaved = savedItems.any { it.itemId == oppId }

                OpportunityDetailDialog(
                    item = opp,
                    isSaved = isSaved,
                    onDismiss = { viewModel.selectedOpportunityForDetail.value = null },
                    onSaveToggle = {
                        when (opp) {
                            is CollegeItem -> viewModel.toggleSaveItem("COLLEGE", opp.id, opp.name, opp.city)
                            is ScholarshipItem -> viewModel.toggleSaveItem("SCHOLARSHIP", opp.id, opp.title, opp.provider)
                            is ExamItem -> viewModel.toggleSaveItem("EXAM", opp.id, opp.title, opp.conductingBody)
                        }
                    },
                    onSetReminder = { remTitle, remDate ->
                        viewModel.addReminder("Deadline: $remTitle", remDate, "10:00 AM")
                    },
                    onTrackApplication = { appTitle, appCategory, appTarget, appDeadline, appPortal ->
                        viewModel.trackOpportunity(appTitle, appCategory, appTarget, appDeadline, appPortal)
                    },
                    onReport = { _, _ ->
                        viewModel.selectedOpportunityForDetail.value = null
                    }
                )
            }
        }
    }
}
