package com.example.data.local

import com.example.data.model.AnnouncementItem
import com.example.data.model.CollegeItem
import com.example.data.model.CommunityAnswer
import com.example.data.model.CommunityQuestion
import com.example.data.model.DeadlineItem
import com.example.data.model.DocumentItem
import com.example.data.model.ExamItem
import com.example.data.model.ScholarshipItem
import com.example.data.model.StudentProfile

object DefaultSeedData {

    val defaultProfile = StudentProfile(
        id = 1,
        fullName = "Rohan Sharma",
        email = "rohan.student@example.com",
        state = "Delhi",
        qualification = "12th Standard",
        stream = "Science (PCM)",
        category = "General",
        annualIncomeRange = "₹2.5L - ₹8L",
        marksPercentage = 84.0,
        preferredCourse = "Engineering & Technology",
        isHostelNeeded = true,
        isGuest = false,
        referralCode = "SHUB-DEL782",
        referralCount = 2,
        badgesUnlocked = "PROFILE_COMPLETED,FIRST_SAVE",
        notificationsEnabled = true,
        isDarkMode = false,
        selectedLanguage = "Hinglish"
    )

    val sampleColleges = listOf(
        CollegeItem(
            id = "col_iitd",
            name = "Indian Institute of Technology Delhi (IIT Delhi)",
            university = "Institute of National Importance",
            state = "Delhi",
            city = "New Delhi (Hauz Khas)",
            coursesOffered = "B.Tech, Dual Degree, M.Tech, M.Sc, Ph.D",
            annualFees = "₹2,20,000 / year (Fee waivers for SC/ST/EWS)",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://home.iitd.ac.in",
            rankingInfo = "NIRF Engineering Rank #2",
            cutoffSummary = "JEE Advanced Top 100 - 4500 AIR",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-01",
            viewCount = 14200
        ),
        CollegeItem(
            id = "col_du_srcc",
            name = "Shri Ram College of Commerce (SRCC)",
            university = "University of Delhi (DU)",
            state = "Delhi",
            city = "New Delhi (North Campus)",
            coursesOffered = "B.Com (Hons), B.A. (Hons) Economics, M.Com",
            annualFees = "₹32,000 / year",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://www.srcc.edu",
            rankingInfo = "NIRF College Rank #1 (Commerce)",
            cutoffSummary = "CUET UG Percentile 99.5+ or 780+/800",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-10",
            viewCount = 11500
        ),
        CollegeItem(
            id = "col_aiims_delhi",
            name = "All India Institute of Medical Sciences (AIIMS)",
            university = "Autonomous Central Medical University",
            state = "Delhi",
            city = "New Delhi (Ansari Nagar)",
            coursesOffered = "MBBS, B.Sc Nursing, MD, MS, M.Ch",
            annualFees = "₹1,628 / year (Heavily Subsidized)",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://www.aiims.edu",
            rankingInfo = "NIRF Medical Rank #1",
            cutoffSummary = "NEET UG AIR Top 50 (General)",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-07-28",
            viewCount = 18900
        ),
        CollegeItem(
            id = "col_nitt",
            name = "National Institute of Technology Tiruchirappalli (NIT Trichy)",
            university = "Institute of National Importance (NIT Council)",
            state = "Tamil Nadu",
            city = "Tiruchirappalli",
            coursesOffered = "B.Tech, B.Arch, M.Tech, MBA, MCA",
            annualFees = "₹1,45,000 / year",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://www.nitt.edu",
            rankingInfo = "NIRF Engineering Rank #9",
            cutoffSummary = "JEE Main AIR 1,000 - 15,000 (JoSAA)",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-05",
            viewCount = 8700
        ),
        CollegeItem(
            id = "col_jadavpur",
            name = "Jadavpur University",
            university = "State University (West Bengal)",
            state = "West Bengal",
            city = "Kolkata",
            coursesOffered = "B.E., B.Sc, B.A., M.E., Ph.D",
            annualFees = "₹2,400 / year (Highly Affordable)",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "http://www.jaduniv.edu.in",
            rankingInfo = "NIRF University Rank #4",
            cutoffSummary = "WBJEE GMR Top 100 - 1200",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-12",
            viewCount = 7600
        ),
        CollegeItem(
            id = "col_bhu",
            name = "Banaras Hindu University (BHU)",
            university = "Central University",
            state = "Uttar Pradesh",
            city = "Varanasi",
            coursesOffered = "B.A., B.Sc, B.Com, LLB, B.Tech (IIT-BHU)",
            annualFees = "₹5,000 - ₹25,000 / year",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://www.bhu.ac.in",
            rankingInfo = "NIRF University Rank #5",
            cutoffSummary = "CUET UG / JEE Advanced",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-14",
            viewCount = 9200
        ),
        CollegeItem(
            id = "col_bits_pilani",
            name = "BITS Pilani (Birla Institute of Technology and Science)",
            university = "Deemed to be University / Institute of Eminence",
            state = "Rajasthan",
            city = "Pilani",
            coursesOffered = "B.E. (Hons), M.Sc, M.E., MBA",
            annualFees = "₹5,40,000 / year (Merit-cum-Need Scholarships available)",
            isGovernment = false,
            hostelAvailable = true,
            officialWebsite = "https://www.bits-pilani.ac.in",
            rankingInfo = "NIRF Overall Rank #20",
            cutoffSummary = "BITSAT Score 240 - 330+",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-02",
            viewCount = 13100
        ),
        CollegeItem(
            id = "col_anna_univ",
            name = "College of Engineering, Guindy (Anna University)",
            university = "State University (Tamil Nadu)",
            state = "Tamil Nadu",
            city = "Chennai",
            coursesOffered = "B.E., B.Tech, M.E., MBA",
            annualFees = "₹35,000 / year",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://www.annauniv.edu",
            rankingInfo = "NIRF Engineering Rank #13",
            cutoffSummary = "TNEA Cutoff 195 - 199.5 / 200",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-08",
            viewCount = 6400
        ),
        CollegeItem(
            id = "col_miranda_house",
            name = "Miranda House (University of Delhi)",
            university = "University of Delhi (DU)",
            state = "Delhi",
            city = "New Delhi (North Campus)",
            coursesOffered = "B.A. (Hons), B.Sc (Hons), M.A., M.Sc",
            annualFees = "₹19,000 - ₹22,000 / year (DU Financial Support Scheme: 50%-100% Fee Concessions)",
            isGovernment = true,
            hostelAvailable = true,
            officialWebsite = "https://www.mirandahouse.ac.in",
            rankingInfo = "NIRF All-India College Rank #1 (Consecutive 7 Years)",
            cutoffSummary = "CUET UG Top Percentiles (DU CSAS: admission.uod.ac.in)",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-20",
            viewCount = 10800
        )
    )

    val sampleScholarships = listOf(
        ScholarshipItem(
            id = "sch_nsp_postmatric_sc",
            title = "Post-Matric Scholarship Scheme for SC Students",
            provider = "Ministry of Social Justice & Empowerment (Govt of India)",
            state = "All India",
            eligibleCourses = "Class 11, 12, ITI, Diploma, Graduation, Post Graduation",
            eligibleCategories = "SC/ST",
            maxAnnualIncome = 250000,
            minPercentage = 50.0,
            qualificationRequired = "10th / 12th Passed",
            amountPerYear = "Full Tuition Fee + ₹13,500/year Maintenance Allowance",
            deadline = "2026-10-31",
            officialWebsite = "https://scholarships.gov.in",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-15",
            viewCount = 15300
        ),
        ScholarshipItem(
            id = "sch_nsp_central_sector",
            title = "Central Sector Scheme of Scholarship for College and University Students",
            provider = "Department of Higher Education (MoE, Govt of India)",
            state = "All India",
            eligibleCourses = "Regular Degree Courses (B.A., B.Sc, B.Com, B.Tech, MBBS)",
            eligibleCategories = "All",
            maxAnnualIncome = 450000,
            minPercentage = 80.0,
            qualificationRequired = "12th Standard (Top 20th Percentile of respective Board)",
            amountPerYear = "₹12,000 / year (UG) to ₹20,000 / year (PG)",
            deadline = "2026-11-15",
            officialWebsite = "https://scholarships.gov.in",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-18",
            viewCount = 19400
        ),
        ScholarshipItem(
            id = "sch_aicte_pragati",
            title = "AICTE Pragati Scholarship for Girl Students",
            provider = "All India Council for Technical Education (AICTE)",
            state = "All India",
            eligibleCourses = "Degree / Diploma in Technical Courses (Engineering, Architecture, etc.)",
            eligibleCategories = "Girls",
            maxAnnualIncome = 800000,
            minPercentage = 60.0,
            qualificationRequired = "12th Standard / Diploma",
            amountPerYear = "₹50,000 / year for every year of study",
            deadline = "2026-10-25",
            officialWebsite = "https://www.aicte-india.org",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-12",
            viewCount = 14100
        ),
        ScholarshipItem(
            id = "sch_nsp_postmatric_obc",
            title = "PM-YASASVI Post-Matric Scholarship for OBC, EBC & DNT",
            provider = "Ministry of Social Justice & Empowerment",
            state = "All India",
            eligibleCourses = "All Post-Matric Courses (11th, 12th, Degree, PG)",
            eligibleCategories = "OBC",
            maxAnnualIncome = 250000,
            minPercentage = 50.0,
            qualificationRequired = "10th / 12th Passed",
            amountPerYear = "Compulsory Fees + ₹4,000 to ₹10,000 / year allowance",
            deadline = "2026-11-10",
            officialWebsite = "https://scholarships.gov.in",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-16",
            viewCount = 12900
        ),
        ScholarshipItem(
            id = "sch_reliance_foundation",
            title = "Reliance Foundation Undergraduate Scholarship",
            provider = "Reliance Foundation",
            state = "All India",
            eligibleCourses = "First-year regular full-time UG degrees in any stream",
            eligibleCategories = "All",
            maxAnnualIncome = 1500000,
            minPercentage = 60.0,
            qualificationRequired = "12th Standard with min 60%",
            amountPerYear = "Up to ₹2,00,000 over duration of degree",
            deadline = "2026-10-15",
            officialWebsite = "https://www.scholarships.reliancefoundation.org",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-01",
            viewCount = 16700
        ),
        ScholarshipItem(
            id = "sch_delhi_merit_ews",
            title = "Delhi Govt Merit-cum-Means Financial Assistance Scheme",
            provider = "Directorate of Higher Education, Govt. of NCT of Delhi",
            state = "Delhi",
            eligibleCourses = "Undergraduate Degree in Delhi State Public Universities",
            eligibleCategories = "EWS",
            maxAnnualIncome = 250000,
            minPercentage = 60.0,
            qualificationRequired = "Enrolled in Delhi State University",
            amountPerYear = "100% Tuition Fee Waiver (Income < 2.5L) / 50% (Income 2.5L-6L)",
            deadline = "2026-12-15",
            officialWebsite = "https://edistrict.delhigovt.nic.in",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-11",
            viewCount = 8200
        ),
        ScholarshipItem(
            id = "sch_vidyalaxmi_csis",
            title = "PM Vidyalaxmi Scheme & Central Sector Interest Subsidy (CSIS)",
            provider = "Department of Higher Education (Ministry of Education, Govt of India)",
            state = "All India",
            eligibleCourses = "Higher Education in NIRF Top 100 HEIs, IITs, Central Universities",
            eligibleCategories = "All",
            maxAnnualIncome = 800000,
            minPercentage = 50.0,
            qualificationRequired = "Admitted into approved technical or professional degree",
            amountPerYear = "100% Interest Subsidy on Education Loans up to ₹7.5 Lakh during moratorium period",
            deadline = "2026-12-31",
            officialWebsite = "https://www.vidyalakshmi.co.in",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-25",
            viewCount = 11200
        ),
        ScholarshipItem(
            id = "sch_nmms_scholarship",
            title = "National Means-cum-Merit Scholarship Scheme (NMMSS)",
            provider = "Department of School Education & Literacy (MoE, Govt of India)",
            state = "All India",
            eligibleCourses = "Secondary & Higher Secondary Education (Class 9 to 12)",
            eligibleCategories = "All",
            maxAnnualIncome = 350000,
            minPercentage = 55.0,
            qualificationRequired = "Class 8 Passed from Govt / Aided School with min 55% marks",
            amountPerYear = "₹12,000 / year (₹1,000 per month directly via DBT to bank account)",
            deadline = "2026-11-30",
            officialWebsite = "https://scholarships.gov.in",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-22",
            viewCount = 9800
        )
    )

    val sampleExams = listOf(
        ExamItem(
            id = "exam_cuet_ug",
            title = "CUET (UG) - Common University Entrance Test",
            conductingBody = "National Testing Agency (NTA)",
            eligibility = "Passed Class 12 or appearing in 2026",
            coursesTargeted = "Undergraduate programs across 250+ Central, State & Deemed Universities",
            examDate = "May 15 - May 31, 2026",
            applicationDeadline = "2026-04-05",
            officialWebsite = "https://cuet.samarth.ac.in",
            syllabusSummary = "NCERT Class 12 Syllabus (Languages, Domain Subjects, General Test)",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-14",
            viewCount = 28500
        ),
        ExamItem(
            id = "exam_jee_main",
            title = "JEE (Main) - Joint Entrance Examination",
            conductingBody = "National Testing Agency (NTA)",
            eligibility = "Class 12 with Physics, Chemistry, Maths (PCM)",
            coursesTargeted = "B.Tech/B.E. at NITs, IIITs, GFTIs & Qualifier for JEE Advanced (IITs)",
            examDate = "Session 1: Jan 2026 | Session 2: Apr 2026",
            applicationDeadline = "2026-11-30",
            officialWebsite = "https://jeemain.nta.nic.in",
            syllabusSummary = "Physics, Chemistry, Mathematics (Class 11 & 12 CBSE standard)",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-19",
            viewCount = 31200
        ),
        ExamItem(
            id = "exam_neet_ug",
            title = "NEET (UG) - National Eligibility cum Entrance Test",
            conductingBody = "National Testing Agency (NTA)",
            eligibility = "Class 12 with Physics, Chemistry, Biology (PCB), Min 50% marks",
            coursesTargeted = "MBBS, BDS, BAMS, BHMS, BUMS in all Medical Colleges in India",
            examDate = "May 3, 2026",
            applicationDeadline = "2026-03-15",
            officialWebsite = "https://neet.nta.nic.in",
            syllabusSummary = "Physics, Chemistry, Biology (Botany & Zoology) Class 11 & 12 NCERT",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-07",
            viewCount = 36800
        ),
        ExamItem(
            id = "exam_clat",
            title = "CLAT - Common Law Admission Test",
            conductingBody = "Consortium of National Law Universities (NLUs)",
            eligibility = "Class 12 Passed with min 45% marks (40% for SC/ST)",
            coursesTargeted = "5-Year Integrated BA LLB, BBA LLB at 24 National Law Universities",
            examDate = "December 6, 2026",
            applicationDeadline = "2026-10-20",
            officialWebsite = "https://consortiumofnlus.ac.in",
            syllabusSummary = "English, Current Affairs, Legal Reasoning, Logical Reasoning, Quantitative Techniques",
            verificationStatus = "VERIFIED",
            lastUpdated = "2026-08-04",
            viewCount = 14200
        )
    )

    val sampleDeadlines = listOf(
        DeadlineItem(
            id = "dl_1",
            title = "NSP Post-Matric Scholarship Final Verification",
            categoryType = "SCHOLARSHIP",
            relatedId = "sch_nsp_postmatric_sc",
            deadlineDate = "2026-10-31",
            urgencyTag = "THIS_MONTH",
            officialUrl = "https://scholarships.gov.in",
            notes = "Make sure income certificate is updated after April 1.",
            isReminderSet = true
        ),
        DeadlineItem(
            id = "dl_2",
            title = "Reliance Foundation Scholarship Application Window",
            categoryType = "SCHOLARSHIP",
            relatedId = "sch_reliance_foundation",
            deadlineDate = "2026-10-15",
            urgencyTag = "THIS_WEEK",
            officialUrl = "https://www.scholarships.reliancefoundation.org",
            notes = "Requires 12th marksheet and bonafide college enrollment proof.",
            isReminderSet = true
        ),
        DeadlineItem(
            id = "dl_3",
            title = "JEE Main Session-1 Registration Portal Opens",
            categoryType = "EXAM",
            relatedId = "exam_jee_main",
            deadlineDate = "2026-11-30",
            urgencyTag = "UPCOMING",
            officialUrl = "https://jeemain.nta.nic.in",
            notes = "Check Aadhaar name matches 10th marksheet exact spelling.",
            isReminderSet = false
        ),
        DeadlineItem(
            id = "dl_4",
            title = "Delhi State Merit Scholarship Form Submission",
            categoryType = "SCHOLARSHIP",
            relatedId = "sch_delhi_merit_ews",
            deadlineDate = "2026-12-15",
            urgencyTag = "UPCOMING",
            officialUrl = "https://edistrict.delhigovt.nic.in",
            notes = "Delhi domicile and college fee receipt mandatory.",
            isReminderSet = false
        )
    )

    val sampleDocuments = listOf(
        DocumentItem(
            name = "10th Standard Marksheet & Passing Certificate",
            category = "ACADEMIC",
            isReady = true,
            description = "Crucial for Date of Birth verification in all central exam portals.",
            issuingAuthority = "State Education Board / CBSE / ICSE"
        ),
        DocumentItem(
            name = "12th Standard Marksheet",
            category = "ACADEMIC",
            isReady = true,
            description = "Required for eligibility cutoffs and university admissions.",
            issuingAuthority = "State Education Board / CBSE / ICSE"
        ),
        DocumentItem(
            name = "Aadhaar Card (Linked to Mobile Number)",
            category = "IDENTITY",
            isReady = true,
            description = "Mandatory for National Scholarship Portal (NSP) DBT bank transfer.",
            issuingAuthority = "UIDAI"
        ),
        DocumentItem(
            name = "Annual Income Certificate (Tehsildar / SDM Issued)",
            category = "INCOME_CASTE",
            isReady = false,
            description = "Must be issued after April 1 of current financial year for scholarships.",
            issuingAuthority = "Revenue Dept / Tehsil / District Administration"
        ),
        DocumentItem(
            name = "Caste / Category Certificate (SC/ST/OBC-NCL/EWS)",
            category = "INCOME_CASTE",
            isReady = false,
            description = "OBC-NCL and EWS certificates must be in central government format.",
            issuingAuthority = "District Magistrate / Sub-Divisional Magistrate"
        ),
        DocumentItem(
            name = "Domicile / State Residence Certificate",
            category = "IDENTITY",
            isReady = true,
            description = "Required to claim state quota seats in state universities and state scholarships.",
            issuingAuthority = "Tehsil / State Govt Portal"
        ),
        DocumentItem(
            name = "Passport-size Photographs (White Background)",
            category = "GENERAL",
            isReady = true,
            description = "Recent photos (taken within last 3 months) in standard 3.5 x 4.5 cm.",
            issuingAuthority = "Local Studio / Digital Copy"
        ),
        DocumentItem(
            name = "Active Bank Account in Student's Name (Aadhaar Seeded)",
            category = "FINANCIAL",
            isReady = false,
            description = "NPCI Aadhaar-seeded account required for Direct Benefit Transfer (DBT).",
            issuingAuthority = "Scheduled Commercial Bank / Post Office"
        )
    )

    val sampleQuestions = listOf(
        CommunityQuestion(
            authorName = "Amit Kumar",
            authorQualification = "12th Science",
            title = "Kya OBC-NCL certificate Central format ka hona zaroori hai JEE Main ke liye?",
            body = "State level ka OBC certificate bana hua hai. Kya JEE Main aur JoSAA counselling mein central list wala certificate hi chahiye ya state wala chalega?",
            tag = "Certificates",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 8,
            upvotes = 14,
            answerCount = 2,
            isResolved = true
        ),
        CommunityQuestion(
            authorName = "Pooja Verma",
            authorQualification = "B.Sc 1st Year",
            title = "NSP scholarship mein Renewal student kaise apply karein?",
            body = "Last year fresh apply kiya tha aur scholarship aayi thi. Iss saal portal pe login karne ke baad renewal form mein kon-kon se documents dobara upload karne hain?",
            tag = "Scholarships",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
            upvotes = 9,
            answerCount = 1,
            isResolved = false
        ),
        CommunityQuestion(
            authorName = "Sourabh Roy",
            authorQualification = "12th Commerce",
            title = "CUET UG mein DU B.Com Hons ke liye minimum kitne domain subjects select karne hote hain?",
            body = "Maine Maths nahi padha 12th mein. Kya main B.Com (Pass) ya B.Com (Hons) ke liye eligible hoon DU mein bina Maths ke?",
            tag = "Admissions",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 36,
            upvotes = 11,
            answerCount = 1,
            isResolved = true
        )
    )

    val sampleAnswers = listOf(
        CommunityAnswer(
            questionId = 1,
            authorName = "Vikram Aditya",
            authorRole = "Verified Senior (NIT Trichy)",
            body = "Haan Amit bhai, bilkul! JoSAA aur CSAB counselling ke liye Central List of OBCs wala certificate mandatory hota hai. Aur ye certificate 1 April 2026 ke baad ka issued hona chahiye. Apne tehsil ya edistrict portal pe Central format ke liye apply kar lijiye abhi se.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 6,
            upvotes = 12,
            isHelpful = true
        ),
        CommunityAnswer(
            questionId = 3,
            authorName = "Dr. Neha Kapoor",
            authorRole = "College Mentor",
            body = "DU B.Com (Hons) ke liye CUET mein ya toh Mathematics compulsory hai ya Accountancy. Agar aapne 12th mein Accountancy padhi hai toh aap Accountancy ke through eligible hain! B.Com (Program) ke liye Maths/Accounts dono mein se koi bhi chalega.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 30,
            upvotes = 8,
            isHelpful = true
        )
    )

    val sampleAnnouncements = listOf(
        AnnouncementItem(
            title = "National Scholarship Portal (NSP) Biometric Authentication Update",
            content = "Students applying for Central Sector scholarships must complete Aadhaar Face-RD biometric authentication at their respective institutions before October 31.",
            date = "2026-08-25",
            isImportant = true
        ),
        AnnouncementItem(
            title = "CUET UG Normalization Policy Clarification",
            content = "NTA has updated the multi-session score normalization guidelines for CUET UG 2026. Percentile alongside normalized NTA scores will be used for university merit lists.",
            date = "2026-08-18",
            isImportant = false
        )
    )
}
