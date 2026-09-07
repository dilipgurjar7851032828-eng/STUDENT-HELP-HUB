package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HubTopBar
import com.example.ui.components.OfficialSourceButton
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.Slate700
import com.example.ui.viewmodel.MainViewModel

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String
)

@Composable
fun HelpCenterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val faqs = listOf(
        FaqItem(
            question = "Kya Student Help Hub kisi bhi admission ya scholarship ki guarantee deta hai?",
            answer = "Bilkul nahi. Student Help Hub sirf authenticated information, verified deadlines aur official government portals ki jankari pradan karta hai. Admission aur scholarships strictly merit, reservation criteria aur counseling procedures par aadharit hote hain.",
            category = "Authenticity"
        ),
        FaqItem(
            question = "NSP (National Scholarship Portal) par apply karne ke liye kya zaroori hai?",
            answer = "NSP par apply karne ke liye aapka Aadhaar card, Aadhaar-seeded bank account (NPCI mapped), 1st April ke baad jari kiya gaya aay praman patra (Income certificate), aur sansthan se bonafide certificate anivarya hota hai.",
            category = "Scholarships"
        ),
        FaqItem(
            question = "Central quota admissions mein state OBC certificate chalega?",
            answer = "Nahi. Central universities, IITs, NITs aur central exams (JEE, NEET, CUET) ke liye OBC-NCL certificate Central List ke prescribed format mein hona anivarya hai aur ye 1st April 2026 ke baad ka issued hona chahiye.",
            category = "Certificates"
        ),
        FaqItem(
            question = "App mein voice search kaise kaam karta hai?",
            answer = "Home screen par mic icon dabayein ya natural language mein type karein (jaise: '12th ke baad Delhi mein government college batao'). System aapke query ko parse karke relevant filters automatically apply kar dega.",
            category = "App Features"
        ),
        FaqItem(
            question = "Mera personal data app mein kitna surakshit hai?",
            answer = "Aapka data local device par Room Database mein secure rehta hai. Student Help Hub aapka vyaktigat data kisi third party ko share nahi karta. Aap Profile section mein jakar kabhi bhi apna sara data clear kar sakte hain.",
            category = "Privacy"
        )
    )

    val filteredFaqs = faqs.filter {
        searchQuery.isBlank() ||
                it.question.contains(searchQuery, ignoreCase = true) ||
                it.answer.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HubTopBar(
            title = "Help Center & FAQs",
            subtitle = "Student guidance and official contact assistance",
            onBackClick = { viewModel.navigateBack() }
        )

        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search help topics, NSP rules, certificates...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandIndigo) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("help_search_input")
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "National Education Helplines",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "NSP Helpline: 0120-6619540 • NTA (CUET/JEE): 011-40759000",
                                fontSize = 12.sp,
                                color = Slate700
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(filteredFaqs) { faq ->
                FaqAccordionCard(faq = faq)
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun FaqAccordionCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = BrandIndigo
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
