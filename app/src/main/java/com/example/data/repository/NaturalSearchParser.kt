package com.example.data.repository

data class ParsedSearchIntent(
    val query: String,
    val targetType: String, // "ALL", "COLLEGE", "SCHOLARSHIP", "EXAM", "DOCUMENT"
    val stateFilter: String? = null,
    val isGovernmentOnly: Boolean? = null,
    val courseFilter: String? = null,
    val categoryFilter: String? = null,
    val explanation: String
)

object NaturalSearchParser {

    fun parse(rawQuery: String): ParsedSearchIntent {
        val q = rawQuery.lowercase().trim()

        var targetType = "ALL"
        if (q.contains("college") || q.contains("university") || q.contains("campus") || q.contains("dakhila")) {
            targetType = "COLLEGE"
        } else if (q.contains("scholarship") || q.contains("chhatravritti") || q.contains("paisa") || q.contains("stipend") || q.contains("waiver")) {
            targetType = "SCHOLARSHIP"
        } else if (q.contains("exam") || q.contains("pariksha") || q.contains("entrance") || q.contains("cuet") || q.contains("jee") || q.contains("neet")) {
            targetType = "EXAM"
        } else if (q.contains("document") || q.contains("kagaz") || q.contains("certificate") || q.contains("praman patra")) {
            targetType = "DOCUMENT"
        }

        // Detect State
        val states = listOf("Delhi", "Tamil Nadu", "West Bengal", "Uttar Pradesh", "Rajasthan", "Maharashtra", "Bihar", "Punjab", "Haryana", "Karnataka")
        var detectedState: String? = null
        for (state in states) {
            if (q.contains(state.lowercase())) {
                detectedState = state
                break
            }
        }

        // Detect Govt vs Private
        var isGovt: Boolean? = null
        if (q.contains("government") || q.contains("sarkari") || q.contains("govt") || q.contains("public")) {
            isGovt = true
        } else if (q.contains("private")) {
            isGovt = false
        }

        // Detect Course / Stream
        var course: String? = null
        if (q.contains("engineering") || q.contains("b.tech") || q.contains("btech") || q.contains("iit")) {
            course = "Engineering"
        } else if (q.contains("medical") || q.contains("mbbs") || q.contains("doctor") || q.contains("neet")) {
            course = "Medical"
        } else if (q.contains("commerce") || q.contains("b.com") || q.contains("bcom") || q.contains("srcc")) {
            course = "Commerce"
        } else if (q.contains("law") || q.contains("llb") || q.contains("clat")) {
            course = "Law"
        }

        // Detect Category
        var category: String? = null
        if (q.contains("obc")) category = "OBC"
        else if (q.contains("sc") || q.contains("st")) category = "SC/ST"
        else if (q.contains("ews")) category = "EWS"
        else if (q.contains("girl") || q.contains("female") || q.contains("mahila")) category = "Girls"

        val explanations = mutableListOf<String>()
        if (targetType != "ALL") explanations.add("Category: $targetType")
        if (detectedState != null) explanations.add("Location: $detectedState")
        if (isGovt == true) explanations.add("Institution: Government")
        if (course != null) explanations.add("Domain: $course")
        if (category != null) explanations.add("Reservation: $category")

        val explanationText = if (explanations.isNotEmpty()) {
            "Parsed filters: " + explanations.joinToString(", ")
        } else {
            "Searching across all verified opportunities for \"$rawQuery\""
        }

        return ParsedSearchIntent(
            query = rawQuery,
            targetType = targetType,
            stateFilter = detectedState,
            isGovernmentOnly = isGovt,
            courseFilter = course,
            categoryFilter = category,
            explanation = explanationText
        )
    }
}
