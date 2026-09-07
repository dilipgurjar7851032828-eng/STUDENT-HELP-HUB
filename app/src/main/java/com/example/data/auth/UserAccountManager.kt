package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.StudentProfile
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

data class UserAccount(
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val state: String = "Delhi",
    val qualification: String = "12th Standard",
    val stream: String = "Science (PCM)",
    val category: String = "General",
    val annualIncomeRange: String = "₹2.5L - ₹8L",
    val marksPercentage: Double = 75.0,
    val isDemo: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

sealed class AuthResult {
    data class Success(val account: UserAccount, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Manages user accounts, authentication sessions, and strict data isolation
 * between demo accounts, guest browsing, and distinct student accounts.
 */
class UserAccountManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "student_hub_auth_prefs"
        private const val KEY_ACCOUNTS = "registered_accounts_json"
        private const val KEY_ACTIVE_USER_EMAIL = "active_user_email"
        private const val KEY_IS_GUEST_MODE = "is_guest_mode"

        const val DEMO_EMAIL = "demo@studenthub.org"
        const val DEMO_PASSWORD = "demo123"

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    init {
        // Ensure Demo account is registered in storage if not already present
        val accounts = getAllAccounts().toMutableList()
        if (accounts.none { it.email.equals(DEMO_EMAIL, ignoreCase = true) }) {
            accounts.add(
                UserAccount(
                    email = DEMO_EMAIL,
                    fullName = "Rohan Sharma",
                    passwordHash = hashPassword(DEMO_PASSWORD),
                    state = "Delhi",
                    qualification = "12th Standard",
                    stream = "Science (PCM)",
                    category = "General",
                    annualIncomeRange = "₹2.5L - ₹8L",
                    marksPercentage = 84.0,
                    isDemo = true
                )
            )
            saveAccounts(accounts)
        }
    }

    fun getAllAccounts(): List<UserAccount> {
        val jsonStr = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<UserAccount>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    UserAccount(
                        email = obj.getString("email"),
                        fullName = obj.getString("fullName"),
                        passwordHash = obj.getString("passwordHash"),
                        state = obj.optString("state", "Delhi"),
                        qualification = obj.optString("qualification", "12th Standard"),
                        stream = obj.optString("stream", "Science (PCM)"),
                        category = obj.optString("category", "General"),
                        annualIncomeRange = obj.optString("annualIncomeRange", "₹2.5L - ₹8L"),
                        marksPercentage = obj.optDouble("marksPercentage", 75.0),
                        isDemo = obj.optBoolean("isDemo", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveAccounts(accounts: List<UserAccount>) {
        val array = JSONArray()
        for (acc in accounts) {
            val obj = JSONObject().apply {
                put("email", acc.email)
                put("fullName", acc.fullName)
                put("passwordHash", acc.passwordHash)
                put("state", acc.state)
                put("qualification", acc.qualification)
                put("stream", acc.stream)
                put("category", acc.category)
                put("annualIncomeRange", acc.annualIncomeRange)
                put("marksPercentage", acc.marksPercentage)
                put("isDemo", acc.isDemo)
                put("createdAt", acc.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_ACCOUNTS, array.toString()).apply()
    }

    fun getActiveUserEmail(): String? {
        return prefs.getString(KEY_ACTIVE_USER_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return getActiveUserEmail() != null
    }

    fun isGuestMode(): Boolean {
        return prefs.getBoolean(KEY_IS_GUEST_MODE, false)
    }

    fun setGuestMode(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_GUEST_MODE, enabled)
            .apply()
    }

    fun getActiveAccount(): UserAccount? {
        val email = getActiveUserEmail() ?: return null
        return getAllAccounts().firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    fun getActiveUser(): UserAccount? = getActiveAccount()

    fun registerNewAccount(
        fullName: String,
        email: String,
        password: String,
        state: String,
        qualification: String,
        stream: String,
        category: String,
        annualIncomeRange: String,
        marksPercentage: Double
    ): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = fullName.trim()

        if (trimmedName.isBlank()) {
            return AuthResult.Error("Kripya apna poora naam darj karein (Full name is required)")
        }
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return AuthResult.Error("Kripya valid email address darj karein (e.g. yourname@example.com)")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password kam se kam 6 aksharon ka hona chahiye (Minimum 6 characters)")
        }

        val accounts = getAllAccounts().toMutableList()
        if (accounts.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            return AuthResult.Error("Yeh email address pehle se registered hai. Kripya Login karein.")
        }

        val newAccount = UserAccount(
            email = trimmedEmail,
            fullName = trimmedName,
            passwordHash = hashPassword(password),
            state = state,
            qualification = qualification,
            stream = stream,
            category = category,
            annualIncomeRange = annualIncomeRange,
            marksPercentage = marksPercentage,
            isDemo = false
        )

        accounts.add(newAccount)
        saveAccounts(accounts)

        // Set this new distinct account as active
        prefs.edit()
            .putString(KEY_ACTIVE_USER_EMAIL, trimmedEmail)
            .putBoolean(KEY_IS_GUEST_MODE, false)
            .apply()

        return AuthResult.Success(newAccount, "Account successfully created!")
    }

    fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank()) {
            return AuthResult.Error("Kripya email address darj karein")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Kripya password darj karein")
        }

        val accounts = getAllAccounts()
        val account = accounts.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) }
            ?: return AuthResult.Error("Yeh account darj nahi hai. Kripya naya account banayein (Sign Up).")

        val inputHash = hashPassword(password)
        if (account.passwordHash != inputHash) {
            return AuthResult.Error("Galat password. Kripya dobara koshish karein ya Forgot Password use karein.")
        }

        // Set as active session
        prefs.edit()
            .putString(KEY_ACTIVE_USER_EMAIL, account.email)
            .putBoolean(KEY_IS_GUEST_MODE, false)
            .apply()

        return AuthResult.Success(account, "Login successful! Swagat hai, ${account.fullName}")
    }

    fun loginDemoAccount(): AuthResult {
        val accounts = getAllAccounts()
        val demoAcc = accounts.firstOrNull { it.email.equals(DEMO_EMAIL, ignoreCase = true) }
            ?: UserAccount(
                email = DEMO_EMAIL,
                fullName = "Rohan Sharma",
                passwordHash = hashPassword(DEMO_PASSWORD),
                isDemo = true
            )

        prefs.edit()
            .putString(KEY_ACTIVE_USER_EMAIL, DEMO_EMAIL)
            .putBoolean(KEY_IS_GUEST_MODE, false)
            .apply()

        return AuthResult.Success(demoAcc, "Demo Account (Rohan Sharma) activated for testing.")
    }

    fun continueAsGuest(): StudentProfile {
        prefs.edit()
            .remove(KEY_ACTIVE_USER_EMAIL)
            .putBoolean(KEY_IS_GUEST_MODE, true)
            .apply()

        return StudentProfile(
            id = 1,
            fullName = "Guest Student",
            email = "",
            state = "All India",
            qualification = "12th Standard",
            stream = "General",
            category = "General",
            annualIncomeRange = "₹2.5L - ₹8L",
            marksPercentage = 75.0,
            isGuest = true,
            referralCode = "",
            referralCount = 0,
            badgesUnlocked = ""
        )
    }

    fun resetPassword(email: String, newPassword: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return AuthResult.Error("Kripya valid registered email address darj karein")
        }
        if (newPassword.length < 6) {
            return AuthResult.Error("Naya password kam se kam 6 aksharon ka hona chahiye")
        }

        val accounts = getAllAccounts().toMutableList()
        val idx = accounts.indexOfFirst { it.email.equals(trimmedEmail, ignoreCase = true) }
        if (idx == -1) {
            return AuthResult.Error("Yeh email address system mein nahi mila. Kripya registered email darj karein.")
        }

        val existing = accounts[idx]
        val updated = existing.copy(passwordHash = hashPassword(newPassword))
        accounts[idx] = updated
        saveAccounts(accounts)

        return AuthResult.Success(updated, "Password successfully reset! Ab aap naye password se Login kar sakte hain.")
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_ACTIVE_USER_EMAIL)
            .putBoolean(KEY_IS_GUEST_MODE, false)
            .apply()
    }
}
