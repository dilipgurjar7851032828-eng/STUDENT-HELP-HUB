package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AnnouncementItem
import com.example.data.model.ApplicationItem
import com.example.data.model.CollegeItem
import com.example.data.model.CommunityAnswer
import com.example.data.model.CommunityQuestion
import com.example.data.model.DeadlineItem
import com.example.data.model.DocumentItem
import com.example.data.model.ExamItem
import com.example.data.model.ReminderItem
import com.example.data.model.ReportItem
import com.example.data.model.SavedItem
import com.example.data.model.ScholarshipItem
import com.example.data.model.StudentProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentProfile::class,
        CollegeItem::class,
        ScholarshipItem::class,
        ExamItem::class,
        DeadlineItem::class,
        ApplicationItem::class,
        DocumentItem::class,
        ReminderItem::class,
        SavedItem::class,
        CommunityQuestion::class,
        CommunityAnswer::class,
        ReportItem::class,
        AnnouncementItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentHubDao(): StudentHubDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Ensure all tables exist without altering or dropping existing tables
                db.execSQL("CREATE TABLE IF NOT EXISTS `student_profiles` (`id` INTEGER NOT NULL, `fullName` TEXT NOT NULL, `email` TEXT NOT NULL, `state` TEXT NOT NULL, `qualification` TEXT NOT NULL, `stream` TEXT NOT NULL, `category` TEXT NOT NULL, `annualIncomeRange` TEXT NOT NULL, `marksPercentage` REAL NOT NULL, `preferredCourse` TEXT NOT NULL, `isHostelNeeded` INTEGER NOT NULL, `isGuest` INTEGER NOT NULL, `referralCode` TEXT NOT NULL, `referralCount` INTEGER NOT NULL, `badgesUnlocked` TEXT NOT NULL, `notificationsEnabled` INTEGER NOT NULL, `isDarkMode` INTEGER NOT NULL, `selectedLanguage` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `colleges` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `university` TEXT NOT NULL, `state` TEXT NOT NULL, `city` TEXT NOT NULL, `coursesOffered` TEXT NOT NULL, `annualFees` TEXT NOT NULL, `isGovernment` INTEGER NOT NULL, `hostelAvailable` INTEGER NOT NULL, `officialWebsite` TEXT NOT NULL, `rankingInfo` TEXT NOT NULL, `cutoffSummary` TEXT NOT NULL, `verificationStatus` TEXT NOT NULL, `lastUpdated` TEXT NOT NULL, `viewCount` INTEGER NOT NULL, `admissionDeadline` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `scholarships` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `provider` TEXT NOT NULL, `state` TEXT NOT NULL, `eligibleCourses` TEXT NOT NULL, `eligibleCategories` TEXT NOT NULL, `maxAnnualIncome` INTEGER NOT NULL, `minPercentage` REAL NOT NULL, `qualificationRequired` TEXT NOT NULL, `amountPerYear` TEXT NOT NULL, `deadline` TEXT NOT NULL, `officialWebsite` TEXT NOT NULL, `verificationStatus` TEXT NOT NULL, `lastUpdated` TEXT NOT NULL, `viewCount` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `exams` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `conductingBody` TEXT NOT NULL, `eligibility` TEXT NOT NULL, `coursesTargeted` TEXT NOT NULL, `examDate` TEXT NOT NULL, `applicationDeadline` TEXT NOT NULL, `officialWebsite` TEXT NOT NULL, `syllabusSummary` TEXT NOT NULL, `verificationStatus` TEXT NOT NULL, `lastUpdated` TEXT NOT NULL, `viewCount` INTEGER NOT NULL, `applicationFee` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `deadlines` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `categoryType` TEXT NOT NULL, `relatedId` TEXT NOT NULL, `deadlineDate` TEXT NOT NULL, `urgencyTag` TEXT NOT NULL, `officialUrl` TEXT NOT NULL, `notes` TEXT NOT NULL, `isReminderSet` INTEGER NOT NULL, `verificationStatus` TEXT NOT NULL, `lastUpdated` TEXT NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `applications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL, `targetName` TEXT NOT NULL, `status` TEXT NOT NULL, `deadlineDate` TEXT NOT NULL, `appliedDate` TEXT NOT NULL, `applicationNumber` TEXT NOT NULL, `notes` TEXT NOT NULL, `portalLink` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `isReady` INTEGER NOT NULL, `description` TEXT NOT NULL, `issuingAuthority` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `targetDate` TEXT NOT NULL, `targetTime` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `relatedCategory` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `saved_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `itemType` TEXT NOT NULL, `itemId` TEXT NOT NULL, `title` TEXT NOT NULL, `subtitle` TEXT NOT NULL, `savedTimestamp` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `community_questions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `authorName` TEXT NOT NULL, `authorQualification` TEXT NOT NULL, `title` TEXT NOT NULL, `body` TEXT NOT NULL, `tag` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `upvotes` INTEGER NOT NULL, `answerCount` INTEGER NOT NULL, `isResolved` INTEGER NOT NULL, `isReported` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `community_answers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `questionId` INTEGER NOT NULL, `authorName` TEXT NOT NULL, `authorRole` TEXT NOT NULL, `body` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `upvotes` INTEGER NOT NULL, `isHelpful` INTEGER NOT NULL, `isReported` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `reports` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `targetType` TEXT NOT NULL, `targetId` TEXT NOT NULL, `reason` TEXT NOT NULL, `details` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `status` TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `announcements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `date` TEXT NOT NULL, `isImportant` INTEGER NOT NULL)")

                // 2. Safely add missing columns to any existing tables, preserving all pre-existing rows
                addColumnIfNotExists(db, "student_profiles", "selectedLanguage", "TEXT NOT NULL DEFAULT 'Hinglish'")

                addColumnIfNotExists(db, "colleges", "verificationStatus", "TEXT NOT NULL DEFAULT 'VERIFIED'")
                addColumnIfNotExists(db, "colleges", "lastUpdated", "TEXT NOT NULL DEFAULT '2026-08-15'")
                addColumnIfNotExists(db, "colleges", "viewCount", "INTEGER NOT NULL DEFAULT 1250")
                addColumnIfNotExists(db, "colleges", "admissionDeadline", "TEXT NOT NULL DEFAULT '2026-09-30'")

                addColumnIfNotExists(db, "scholarships", "verificationStatus", "TEXT NOT NULL DEFAULT 'VERIFIED'")
                addColumnIfNotExists(db, "scholarships", "lastUpdated", "TEXT NOT NULL DEFAULT '2026-08-20'")
                addColumnIfNotExists(db, "scholarships", "viewCount", "INTEGER NOT NULL DEFAULT 3420")

                addColumnIfNotExists(db, "exams", "verificationStatus", "TEXT NOT NULL DEFAULT 'VERIFIED'")
                addColumnIfNotExists(db, "exams", "lastUpdated", "TEXT NOT NULL DEFAULT '2026-08-10'")
                addColumnIfNotExists(db, "exams", "viewCount", "INTEGER NOT NULL DEFAULT 4100")
                addColumnIfNotExists(db, "exams", "applicationFee", "TEXT NOT NULL DEFAULT '₹1,000 (Gen) / ₹900 (OBC) / ₹500 (SC/ST/PwD)'")

                addColumnIfNotExists(db, "deadlines", "notes", "TEXT NOT NULL DEFAULT ''")
                addColumnIfNotExists(db, "deadlines", "isReminderSet", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfNotExists(db, "deadlines", "verificationStatus", "TEXT NOT NULL DEFAULT 'VERIFIED'")
                addColumnIfNotExists(db, "deadlines", "lastUpdated", "TEXT NOT NULL DEFAULT '2026-08-25'")

                addColumnIfNotExists(db, "applications", "appliedDate", "TEXT NOT NULL DEFAULT ''")
                addColumnIfNotExists(db, "applications", "applicationNumber", "TEXT NOT NULL DEFAULT ''")
                addColumnIfNotExists(db, "applications", "notes", "TEXT NOT NULL DEFAULT ''")
                addColumnIfNotExists(db, "applications", "portalLink", "TEXT NOT NULL DEFAULT ''")

                addColumnIfNotExists(db, "documents", "description", "TEXT NOT NULL DEFAULT ''")
                addColumnIfNotExists(db, "documents", "issuingAuthority", "TEXT NOT NULL DEFAULT ''")

                addColumnIfNotExists(db, "reminders", "targetTime", "TEXT NOT NULL DEFAULT '10:00 AM'")
                addColumnIfNotExists(db, "reminders", "isCompleted", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfNotExists(db, "reminders", "relatedCategory", "TEXT NOT NULL DEFAULT 'DEADLINE'")

                addColumnIfNotExists(db, "community_questions", "isResolved", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfNotExists(db, "community_questions", "isReported", "INTEGER NOT NULL DEFAULT 0")

                addColumnIfNotExists(db, "community_answers", "isHelpful", "INTEGER NOT NULL DEFAULT 0")
                addColumnIfNotExists(db, "community_answers", "isReported", "INTEGER NOT NULL DEFAULT 0")
            }

            private fun addColumnIfNotExists(
                db: SupportSQLiteDatabase,
                table: String,
                column: String,
                columnDef: String
            ) {
                val cursor = db.query("PRAGMA table_info(`$table`)")
                var columnExists = false
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    if (nameIndex != -1 && cursor.getString(nameIndex).equals(column, ignoreCase = true)) {
                        columnExists = true
                        break
                    }
                }
                cursor.close()
                if (!columnExists) {
                    db.execSQL("ALTER TABLE `$table` ADD COLUMN `$column` $columnDef")
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_help_hub.db"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate database with default seed data
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = database.studentHubDao()
                                dao.insertProfile(DefaultSeedData.defaultProfile)
                                dao.insertColleges(DefaultSeedData.sampleColleges)
                                dao.insertScholarships(DefaultSeedData.sampleScholarships)
                                dao.insertExams(DefaultSeedData.sampleExams)
                                dao.insertDeadlines(DefaultSeedData.sampleDeadlines)
                                dao.insertDocuments(DefaultSeedData.sampleDocuments)
                                for (q in DefaultSeedData.sampleQuestions) {
                                    dao.insertQuestion(q)
                                }
                                for (a in DefaultSeedData.sampleAnswers) {
                                    dao.insertAnswer(a)
                                }
                                for (ann in DefaultSeedData.sampleAnnouncements) {
                                    dao.insertAnnouncement(ann)
                                }
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
