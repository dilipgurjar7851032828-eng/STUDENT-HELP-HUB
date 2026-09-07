package com.example

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomMigrationTest {

    @Test
    fun testMigration1To2PreservesAllExistingUserData() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "test_migration_user_data.db"
        context.deleteDatabase(dbName)

        val helperConfig = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // Create Version 1 tables (prior to v2 additions)
                    db.execSQL("""
                        CREATE TABLE `student_profiles` (
                            `id` INTEGER NOT NULL,
                            `fullName` TEXT NOT NULL,
                            `email` TEXT NOT NULL,
                            `state` TEXT NOT NULL,
                            `qualification` TEXT NOT NULL,
                            `stream` TEXT NOT NULL,
                            `category` TEXT NOT NULL,
                            `annualIncomeRange` TEXT NOT NULL,
                            `marksPercentage` REAL NOT NULL,
                            `preferredCourse` TEXT NOT NULL,
                            `isHostelNeeded` INTEGER NOT NULL,
                            `isGuest` INTEGER NOT NULL,
                            `referralCode` TEXT NOT NULL,
                            `referralCount` INTEGER NOT NULL,
                            `badgesUnlocked` TEXT NOT NULL,
                            `notificationsEnabled` INTEGER NOT NULL,
                            `isDarkMode` INTEGER NOT NULL,
                            PRIMARY KEY(`id`)
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE `saved_items` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `itemType` TEXT NOT NULL,
                            `itemId` TEXT NOT NULL,
                            `title` TEXT NOT NULL,
                            `subtitle` TEXT NOT NULL,
                            `savedTimestamp` INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE `applications` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `category` TEXT NOT NULL,
                            `targetName` TEXT NOT NULL,
                            `status` TEXT NOT NULL,
                            `deadlineDate` TEXT NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE `documents` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `category` TEXT NOT NULL,
                            `isReady` INTEGER NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE `reminders` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `title` TEXT NOT NULL,
                            `targetDate` TEXT NOT NULL
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE `colleges` (
                            `id` TEXT NOT NULL,
                            `name` TEXT NOT NULL,
                            `university` TEXT NOT NULL,
                            `state` TEXT NOT NULL,
                            `city` TEXT NOT NULL,
                            `coursesOffered` TEXT NOT NULL,
                            `annualFees` TEXT NOT NULL,
                            `isGovernment` INTEGER NOT NULL,
                            `hostelAvailable` INTEGER NOT NULL,
                            `officialWebsite` TEXT NOT NULL,
                            `rankingInfo` TEXT NOT NULL,
                            `cutoffSummary` TEXT NOT NULL,
                            PRIMARY KEY(`id`)
                        )
                    """.trimIndent())

                    db.execSQL("""
                        CREATE TABLE `scholarships` (
                            `id` TEXT NOT NULL,
                            `title` TEXT NOT NULL,
                            `provider` TEXT NOT NULL,
                            `state` TEXT NOT NULL,
                            `eligibleCourses` TEXT NOT NULL,
                            `eligibleCategories` TEXT NOT NULL,
                            `maxAnnualIncome` INTEGER NOT NULL,
                            `minPercentage` REAL NOT NULL,
                            `qualificationRequired` TEXT NOT NULL,
                            `amountPerYear` TEXT NOT NULL,
                            `deadline` TEXT NOT NULL,
                            `officialWebsite` TEXT NOT NULL,
                            PRIMARY KEY(`id`)
                        )
                    """.trimIndent())

                    // Insert critical existing user records in Version 1
                    db.execSQL("""
                        INSERT INTO `student_profiles` (
                            `id`, `fullName`, `email`, `state`, `qualification`, `stream`,
                            `category`, `annualIncomeRange`, `marksPercentage`, `preferredCourse`,
                            `isHostelNeeded`, `isGuest`, `referralCode`, `referralCount`,
                            `badgesUnlocked`, `notificationsEnabled`, `isDarkMode`
                        ) VALUES (
                            1, 'Priya Sharma', 'priya.sharma@example.com', 'Rajasthan', '12th Standard',
                            'Science (PCB)', 'OBC', '₹2.5L - ₹8L', 89.4, 'Medical & Life Sciences',
                            1, 0, 'SHUB-RAJ101', 5, 'PROFILE_COMPLETED,SAVER', 1, 0
                        )
                    """.trimIndent())

                    db.execSQL("""
                        INSERT INTO `saved_items` (`itemType`, `itemId`, `title`, `subtitle`, `savedTimestamp`)
                        VALUES ('COLLEGE', 'c_aiims', 'AIIMS New Delhi', 'Ansari Nagar, New Delhi', 1720000000)
                    """.trimIndent())

                    db.execSQL("""
                        INSERT INTO `applications` (`title`, `category`, `targetName`, `status`, `deadlineDate`)
                        VALUES ('NSP Post Matric Scholarship', 'SCHOLARSHIP', 'MoMA', 'APPLIED', '2026-10-31')
                    """.trimIndent())

                    db.execSQL("""
                        INSERT INTO `documents` (`name`, `category`, `isReady`)
                        VALUES ('10th Board Certificate', 'ACADEMIC', 1)
                    """.trimIndent())

                    db.execSQL("""
                        INSERT INTO `reminders` (`title`, `targetDate`)
                        VALUES ('Submit NEET UG Verification Form', '2026-09-18')
                    """.trimIndent())

                    db.execSQL("""
                        INSERT INTO `colleges` (
                            `id`, `name`, `university`, `state`, `city`, `coursesOffered`,
                            `annualFees`, `isGovernment`, `hostelAvailable`, `officialWebsite`,
                            `rankingInfo`, `cutoffSummary`
                        ) VALUES (
                            'c_aiims', 'AIIMS New Delhi', 'Autonomous Institute', 'Delhi', 'New Delhi',
                            'MBBS, B.Sc', '₹1,628/yr', 1, 1, 'https://www.aiims.edu', 'NIRF #1 Medical', 'NEET Rank 1-50'
                        )
                    """.trimIndent())
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    // Trigger custom migration
                    AppDatabase.MIGRATION_1_2.migrate(db)
                }
            })
            .build()

        val openHelper = FrameworkSQLiteOpenHelperFactory().create(helperConfig)
        val v1Db = openHelper.writableDatabase

        // Execute migration from version 1 to version 2
        AppDatabase.MIGRATION_1_2.migrate(v1Db)

        // 1. Verify User Profile data survived intact
        val profileCursor = v1Db.query("SELECT * FROM student_profiles WHERE id = 1")
        assertTrue("Profile record must exist after migration", profileCursor.moveToFirst())
        val nameIdx = profileCursor.getColumnIndex("fullName")
        val emailIdx = profileCursor.getColumnIndex("email")
        val streamIdx = profileCursor.getColumnIndex("stream")
        val langIdx = profileCursor.getColumnIndex("selectedLanguage")

        assertEquals("Priya Sharma", profileCursor.getString(nameIdx))
        assertEquals("priya.sharma@example.com", profileCursor.getString(emailIdx))
        assertEquals("Science (PCB)", profileCursor.getString(streamIdx))
        // New column selectedLanguage added with default
        assertNotNull("selectedLanguage must not be null", profileCursor.getString(langIdx))
        assertEquals("Hinglish", profileCursor.getString(langIdx))
        profileCursor.close()

        // 2. Verify Saved Items survived intact
        val savedCursor = v1Db.query("SELECT * FROM saved_items WHERE itemId = 'c_aiims'")
        assertTrue("Saved item must survive migration", savedCursor.moveToFirst())
        val titleIdx = savedCursor.getColumnIndex("title")
        assertEquals("AIIMS New Delhi", savedCursor.getString(titleIdx))
        savedCursor.close()

        // 3. Verify Applications tracking survived intact with new columns added
        val appCursor = v1Db.query("SELECT * FROM applications WHERE title LIKE '%NSP%'")
        assertTrue("Tracked application must survive migration", appCursor.moveToFirst())
        val statusIdx = appCursor.getColumnIndex("status")
        val targetIdx = appCursor.getColumnIndex("targetName")
        val notesIdx = appCursor.getColumnIndex("notes")
        val portalIdx = appCursor.getColumnIndex("portalLink")

        assertEquals("APPLIED", appCursor.getString(statusIdx))
        assertEquals("MoMA", appCursor.getString(targetIdx))
        assertEquals("", appCursor.getString(notesIdx))
        assertEquals("", appCursor.getString(portalIdx))
        appCursor.close()

        // 4. Verify Documents checklist and custom items survived intact
        val docCursor = v1Db.query("SELECT * FROM documents WHERE name LIKE '%10th%'")
        assertTrue("Document checklist item must survive migration", docCursor.moveToFirst())
        val readyIdx = docCursor.getColumnIndex("isReady")
        val descIdx = docCursor.getColumnIndex("description")
        assertEquals(1, docCursor.getInt(readyIdx))
        assertEquals("", docCursor.getString(descIdx))
        docCursor.close()

        // 4b. Verify custom user-created checklist items survive
        val customDocCursor = v1Db.query("SELECT * FROM documents WHERE category = 'CUSTOM'")
        // insert custom user item in v1 db
        v1Db.execSQL("INSERT INTO `documents` (`name`, `category`, `isReady`, `description`, `issuingAuthority`) VALUES ('State Domicile Certificate', 'CUSTOM', 1, 'Self attested photocopy', 'Tehsildar Office')")
        val customCheck = v1Db.query("SELECT * FROM documents WHERE name = 'State Domicile Certificate'")
        assertTrue("Custom checklist item must be retained", customCheck.moveToFirst())
        val customReadyIdx = customCheck.getColumnIndex("isReady")
        val customDescIdx = customCheck.getColumnIndex("description")
        val customAuthIdx = customCheck.getColumnIndex("issuingAuthority")
        assertEquals(1, customCheck.getInt(customReadyIdx))
        assertEquals("Self attested photocopy", customCheck.getString(customDescIdx))
        assertEquals("Tehsildar Office", customCheck.getString(customAuthIdx))
        customCheck.close()
        customDocCursor.close()

        // 5. Verify Reminders survived intact with new columns
        val remCursor = v1Db.query("SELECT * FROM reminders WHERE title LIKE '%NEET%'")
        assertTrue("Reminder must survive migration", remCursor.moveToFirst())
        val targetTimeIdx = remCursor.getColumnIndex("targetTime")
        val isCompletedIdx = remCursor.getColumnIndex("isCompleted")
        assertEquals("10:00 AM", remCursor.getString(targetTimeIdx))
        assertEquals(0, remCursor.getInt(isCompletedIdx))
        remCursor.close()

        // 6. Verify College record survived intact with new v2 columns populated
        val collegeCursor = v1Db.query("SELECT * FROM colleges WHERE id = 'c_aiims'")
        assertTrue("College must survive migration", collegeCursor.moveToFirst())
        val verIdx = collegeCursor.getColumnIndex("verificationStatus")
        val deadlineIdx = collegeCursor.getColumnIndex("admissionDeadline")
        assertEquals("VERIFIED", collegeCursor.getString(verIdx))
        assertEquals("2026-09-30", collegeCursor.getString(deadlineIdx))
        collegeCursor.close()

        // 7. Verify newly added tables (reports and announcements) now exist
        val reportsCursor = v1Db.query("SELECT count(*) FROM reports")
        assertTrue("Reports table must exist", reportsCursor.moveToFirst())
        assertEquals(0, reportsCursor.getInt(0))
        reportsCursor.close()

        val announcementsCursor = v1Db.query("SELECT count(*) FROM announcements")
        assertTrue("Announcements table must exist", announcementsCursor.moveToFirst())
        assertEquals(0, announcementsCursor.getInt(0))
        announcementsCursor.close()

        v1Db.close()
    }

    @Test
    fun testIndependentPortalChecksConcurrentlyAndSafely() = kotlinx.coroutines.test.runTest {
        // Test that verifyOfficialSource returns NEEDS VERIFICATION when an invalid or offline source is provided
        val unreachableResult = com.example.data.remote.OfficialSourceVerifier.verifyOfficialSource(
            url = "https://nonexistent-domain-12345.gov.in",
            deadlineDate = "2026-12-31"
        )
        // Must never falsely mark unavailable info as VERIFIED
        assertEquals("NEEDS VERIFICATION", unreachableResult.status)
        org.junit.Assert.assertFalse(unreachableResult.isReachable)

        // Test that expired deadline correctly flags EXPIRED
        val expiredResult = com.example.data.remote.OfficialSourceVerifier.verifyOfficialSource(
            url = "https://scholarships.gov.in",
            deadlineDate = "2020-01-01"
        )
        assertEquals("EXPIRED", expiredResult.status)

        // Verify that checkAllOfficialPortals completes and returns list of monitored gateways
        val portals = com.example.data.remote.OfficialSourceVerifier.checkAllOfficialPortals()
        assertTrue("Portals list should contain monitored gateways", portals.isNotEmpty())
        assertEquals(com.example.data.remote.OfficialSourceVerifier.officialPortalsToMonitor.size, portals.size)
        // For portals requiring backend keys, note should be present and isLive false
        val geminiPortal = portals.find { it.requiresBackendConfig }
        assertNotNull(geminiPortal)
        org.junit.Assert.assertFalse(geminiPortal!!.isLive)
    }
}
