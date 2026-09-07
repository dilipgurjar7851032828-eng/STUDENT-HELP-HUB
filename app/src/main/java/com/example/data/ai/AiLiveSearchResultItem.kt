package com.example.data.ai

data class AiLiveSearchResultItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val information: String,
    val eligibility: String,
    val amountOrFees: String,
    val deadline: String,
    val officialSource: String,
    val officialLink: String,
    val lastVerified: String = "2026-09-07",
    val verificationStatus: String = "VERIFIED", // "VERIFIED" or "NEEDS VERIFICATION"
    val categoryType: String = "SCHOLARSHIP", // "SCHOLARSHIP", "ADMISSION", "EXAM", "COLLEGE", "GOVT_SCHEME"
    val isCachedLocally: Boolean = false,
    val disclaimer: String? = null
)

data class AiSearchResponse(
    val query: String,
    val results: List<AiLiveSearchResultItem>,
    val searchSummary: String,
    val isLiveAiGenerated: Boolean = false,
    val detectedLanguage: String = "Hinglish",
    val verifiedPortalsChecked: List<String> = emptyList()
)
